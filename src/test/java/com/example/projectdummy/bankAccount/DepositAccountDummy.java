package com.example.projectdummy.bankAccount;

import com.example.projectdummy.AccountDummyDefault;
import com.example.projectdummy.account.AccountMapper;
import com.example.projectdummy.account.model.BankAccount;
import com.example.projectdummy.account.model.TransactionHistory;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

class DepositAccountDummy extends AccountDummyDefault {

    @Autowired
    AccountMapper accountMapper;
    @Autowired
    EmployeeMapper employeeMapper;
    @Autowired
    DepositMapper depositMapper;
    @Autowired
    CardMapper cardMapper;
    final Long CNT = 10000L;

    @Test //요구불, 상품계약, 체크카드 더미 생성
    void createDemandAccount() {
        List<Long> depositIds = depositMapper.selDemandDepositId();
        List<Long> selEmployee = employeeMapper.selEmployee();
        List<Long> checkCards=cardMapper.findCheckCard();
        LocalDateTime localDateTime = LocalDateTime.of(2010, 1, 1, 0, 1);
        Random random = new Random();
        //final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        //요구불
        for(int i = 0 ; i < CNT ; i++) {
            if(localDateTime.isAfter(LocalDateTime.now())){
                break;
            }
            long demandDepositId = depositIds.get(random.nextInt(depositIds.size()));
            boolean success = false;
            int retryCnt = 0;
            BankAccount account = new BankAccount();

            account.setAccountNum("50401"+kofaker.numerify("########"));
            account.setEmployeeId(selEmployee.get(kofaker.random().nextInt(selEmployee.size())));
            account.setCustId(kofaker.random().nextLong(10001)+1);
            account.setProductId(demandDepositId);
            account.setAccountPassword(kofaker.numerify("####"));
            account.setMoney(0);
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
                savingContractDocument(demandDepositId, account.getAccountId(),"00401", localDateTime);

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
                savingContractDocument(userCard.getCardId(),checkCard.getCheckCardId(),"00403", localDateTime);
                localDateTime = localDateTime.plusDays(kofaker.random().nextInt(7)).plusMinutes(kofaker.random().nextInt(30))
                        .plusHours(kofaker.random().nextInt(23));
            }

        }
    }

}
