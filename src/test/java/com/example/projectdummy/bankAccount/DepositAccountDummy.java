package com.example.projectdummy.bankAccount;

import com.example.projectdummy.AccountDummyDefault;
import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.account.AccountMapper;
import com.example.projectdummy.account.model.BankAccount;
import com.example.projectdummy.card.CardMapper;
import com.example.projectdummy.card.model.CheckCard;
import com.example.projectdummy.card.model.UserCard;
import com.example.projectdummy.employee.EmployeeMapper;
import com.example.projectdummy.productAndDeposit.DepositDuration;
import com.example.projectdummy.productAndDeposit.DepositMapper;
import com.example.projectdummy.productAndDeposit.Product;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

//아직 테스트 안햇음
class DepositAccountDummy extends AccountDummyDefault {

    @Autowired
    AccountMapper accountMapper;
    EmployeeMapper employeeMapper;
    DepositMapper depositMapper;
    CardMapper cardMapper;
    final Long CNT = 100L;

    @Test //요구불 생성
    void createDemandAccount() {
        List<Long> depositIds = depositMapper.selDemandDepositId();
        List<Long> selEmployee = employeeMapper.selEmployee();
        List<Long> checkCards=cardMapper.findCheckCard();
        LocalDate localDate = LocalDate.now();
        Random random = new Random();
        //final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        for(int i = 0 ; i < CNT ; i++) {
            long demandDepositId = depositIds.get(random.nextInt(depositIds.size()));
            boolean success = false;
            int retryCnt = 0;
            BankAccount account = new BankAccount();

            account.setAccountNum("50401"+kofaker.numerify("########"));
            account.setEmployeeId(selEmployee.get(kofaker.random().nextInt(selEmployee.size())));
            account.setCustId(kofaker.random().nextLong(10001)+1);
            account.setProductId(demandDepositId);
            account.setAccountPassword(kofaker.numerify("####"));
            account.setMoney(random.nextInt(1_000_000_000));
            account.setStatusCode("00201");
            //계좌 중복시 10번까지는 계좌번호 바꾸며 새로 넣도록 함.
            while(!success && retryCnt < 10) {
                try {
                    accountMapper.saveBankAccount(account);
                    success = true;
                } catch (Exception e) {
                    retryCnt++;
                    account.setAccountNum("50401"+kofaker.numerify("########"));
                    if (isDuplicateKeyException(e)) {
                        System.out.println("Duplicate accountNum detected. Retrying... (" + retryCnt + ")");
                    } else {
                        e.printStackTrace(); // 다른 예외는 출력
                        break; // 중복 외 에러 발생 시 반복 중단
                    }
                }
            }
            //체크카드 insert
            if(success) {
                int cardRetryCnt = 0;
                boolean cardSuccess = false;
                CheckCard checkCard = CheckCard.builder()
                        .accountId(account.getAccountId())
                        .build();
                cardMapper.saveCheckCard(checkCard);

                UserCard userCard = UserCard.builder()
                            .uCardId(checkCard.getCheckCardId())
                            .cardId(checkCards.get(new Random().nextInt(checkCards.size())))
                            .custId(account.getCustId())
                            .cardNumber("502502"+kofaker.numerify("##########"))
                            .uCardCode("00801")
                            .cardPassword(kofaker.numerify("####"))
                            .endAt(localDate.plusYears(3).toString())
                            .build();
                while (cardRetryCnt < 3&&!cardSuccess) {
                    try {
                        cardMapper.saveDepositCard(userCard);
                        cardSuccess = true;
                    }catch (Exception e) {
                        cardRetryCnt++;
                        userCard.setCardNumber("502502"+kofaker.numerify("##########"));
                        if (isDuplicateKeyException(e)) {
                            System.out.println("Duplicate accountNum detected. Retrying... (" + retryCnt + ")");
                        } else {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            }

            //계좌 개설 서류 저장
            savingAccountDocument(demandDepositId, account.getAccountId());

        }
    }

    @Test //저축 계정 생성
    void createSavingAccount() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        List<Long> savingDepositIds = depositMapper.selSavingDepositId();
        List<Long> employees = employeeMapper.selEmployee();
        List<DepositDuration> durations= depositMapper.selDepositDuration();
        Random random = new Random();
        LocalDate localDate = LocalDate.now();

        for(int i = 0 ; i < CNT ; i++) {

        }



    }



    boolean isDuplicateKeyException(Exception e) {
        return e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate");
    }
}
