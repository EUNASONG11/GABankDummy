package com.example.projectdummy.bankAccount;

import com.example.projectdummy.AccountDummyDefault;
import com.example.projectdummy.account.AccountMapper;
import com.example.projectdummy.account.model.BankAccount;
import com.example.projectdummy.card.CardMapper;
import com.example.projectdummy.card.model.CheckCard;
import com.example.projectdummy.card.model.UserCard;
import com.example.projectdummy.employee.EmployeeMapper;
import com.example.projectdummy.productAndDeposit.*;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Test //요구불, 상품계약, 체크카드 더미 생성
    void createDemandAccount() {
        List<Long> depositIds = depositMapper.selDemandDepositId();
        List<Long> selEmployee = employeeMapper.selEmployee();
        List<Long> checkCards=cardMapper.findCheckCard();
        LocalDateTime localDateTime = LocalDateTime.of(2018, 1, 1, 0, 1);
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
            account.setCreatedAt(localDateTime);
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

            if(success) {
                //계좌 개설 서류 저장
                savingContractDocument(demandDepositId, account.getAccountId(),0);

                //체크카드 insert
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
                            .createdAt(localDateTime)
                            .endAt(localDateTime.plusYears(3).toString())
                            .build();
                while (cardRetryCnt < 3&&!cardSuccess) {
                    try {
                        cardMapper.saveDepositCard(userCard);
                        cardSuccess = true;
                    }catch (Exception e) {
                        cardRetryCnt++;
                        userCard.setCardNumber("502502"+kofaker.numerify("##########"));
                        if (isDuplicateKeyException(e)) {
                            System.out.println("Duplicate accountNum detected. Retrying... (" + cardRetryCnt + ")");
                        } else {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
                //카드 개설 서류 저장
                savingContractDocument(userCard.getCardId(),checkCard.getCheckCardId(),1);
                localDateTime = localDateTime.plusDays(kofaker.random().nextInt(7)).plusMinutes(kofaker.random().nextInt(30))
                        .plusHours(kofaker.random().nextInt(23));
            }

        }
    }

    @Test //저축 계좌 생성
    void createSavingAccount() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        List<SavingsDeposit> savingDeposits = depositMapper.selSavingDeposit();
        List<Long> employees = employeeMapper.selEmployee();
        List<DepositDuration> durations= depositMapper.selDepositDuration();
        Random random = new Random();
        LocalDateTime localDateTime = LocalDateTime.of(2018, 1, 1, 0, 1);

        for(int i = 0 ; i < CNT ; i++) {
            SavingsDeposit demandDeposit = savingDeposits.get(random.nextInt(savingDeposits.size()));
            DepositDuration duration = durations.get(random.nextInt(durations.size()));
            boolean success = false;
            int retryCnt = 0;
            BankAccount account = new BankAccount();
            DepositAccount depositAccount=new DepositAccount();

            // deposit_account의 우대금리: 예금 테이블의 우대금리 이하의 랜덤 값. 정수로 바꿨다가 바꿔 넣을 것.

            int money;
            if(demandDeposit.getDepositCode().equals("00503")){ //정기예금이라면

            }else if(demandDeposit.getDepositCode().equals("00504")){ //적금이라면

            }

            account.setAccountNum("50403"+kofaker.numerify("########"));
            account.setEmployeeId(employees.get(kofaker.random().nextInt(employees.size())));
            account.setCustId(kofaker.random().nextLong(10001)+1);
            account.setProductId(demandDeposit.getDepositId());
            account.setAccountPassword(kofaker.numerify("####"));
            //account.setMoney(money);
            account.setStatusCode("00201");

            while(!success && retryCnt < 10) {
                try {
                    accountMapper.saveBankAccount(account);
                    success = true;
                }catch (Exception e) {
                    retryCnt++;
                    account.setAccountNum("50403"+kofaker.numerify("########"));
                    if (isDuplicateKeyException(e)) {
                        System.out.println("Duplicate accountNum detected. Retrying... (" + retryCnt + ")");
                    } else {
                        e.printStackTrace();
                        break;
                    }
                }
            }
                BigDecimal discountedRate = depositAccount.getDiscountedRate(); //이 아래의 랜덤값을 넣도록 추후 수정
                LocalDateTime endAt=localDateTime.plusMonths(duration.getDuration());

                depositAccount.setAccountId(account.getAccountId());
                depositAccount.setDepositDurationId(duration.getDepositDurationId());
                depositAccount.setDiscountedRate(discountedRate);
                depositAccount.setEndAt(endAt);
                if(endAt.isBefore(LocalDateTime.now())) { //endAt이 현재보다 이전이라면 endAt과 같게. 아니면 null로 둠.
                    depositAccount.setCancelDate(endAt);
                }


        }



    }



    boolean isDuplicateKeyException(Exception e) {
        return e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate");
    }
}
