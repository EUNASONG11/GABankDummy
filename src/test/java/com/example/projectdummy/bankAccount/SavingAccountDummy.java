package com.example.projectdummy.bankAccount;

import com.example.projectdummy.AccountDummyDefault;
import com.example.projectdummy.account.AccountMapper;
import com.example.projectdummy.account.HistoryMapper;
import com.example.projectdummy.account.model.BankAccount;
import com.example.projectdummy.account.model.TransactionHistory;
import com.example.projectdummy.card.CardMapper;
import com.example.projectdummy.employee.EmployeeMapper;
import com.example.projectdummy.productAndDeposit.DepositAccount;
import com.example.projectdummy.productAndDeposit.DepositDuration;
import com.example.projectdummy.productAndDeposit.DepositMapper;
import com.example.projectdummy.productAndDeposit.SavingsDeposit;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SavingAccountDummy extends AccountDummyDefault {
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    EmployeeMapper employeeMapper;
    @Autowired
    DepositMapper depositMapper;
    @Autowired
    HistoryMapper historyMapper;

    final Long CNT = 10000L;

    @Test //저축 계좌 생성
    void createSavingAccount() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        List<SavingsDeposit> savingDeposits = depositMapper.selSavingDeposit();
        List<Long> employees = employeeMapper.selEmployee();
        List<DepositDuration> durations= depositMapper.selDepositDuration();
        Random random = new Random();
        LocalDateTime localDateTime = LocalDateTime.of(2025, 1, 1, 0, 1);
        List<TransactionHistory> histories = new ArrayList<>();
        List<Long> selTranFeeId = accountMapper.selTranFeeId();

        for(int i = 0 ; i < CNT ; i++) {
            SavingsDeposit demandDeposit = savingDeposits.get(random.nextInt(savingDeposits.size()));
            DepositDuration duration = durations.get(random.nextInt(durations.size()));
            boolean success = false;
            int retryCnt = 0;
            BankAccount account = new BankAccount();
            DepositAccount depositAccount=new DepositAccount();

            // deposit_account의 우대금리(discountedRate): 예금 테이블의 우대금리 이하의 랜덤 값.
            // 정기예금인지 적금인지에 따라 입출금 기록도 필요할듯?
            double discount=random.nextDouble()*depositAccount.getDiscountedRate().doubleValue();
            BigDecimal discountedRate = new BigDecimal(discount).setScale(1, RoundingMode.DOWN);

            LocalDateTime endAt=localDateTime.plusMonths(duration.getDuration());
            int postMoney;
            int money;
            int bet = 0;
            int transCase = 0;
            if(demandDeposit.getDepositCode().equals("00503")) { //정기예금이라면
                if (endAt.isBefore(LocalDateTime.now())) { //만료되었다면
                    postMoney = kofaker.random().nextInt(1_000_000_000);
                    money = (int) (postMoney * (discount + duration.getEndInterest().doubleValue())) + postMoney;
                    depositAccount.setCancelDate(endAt); //endAt이 현재보다 이전이라면 endAt과 같게. 아니면 null로 둠.
                    transCase = 1;
                } else {
                    money = postMoney = kofaker.random().nextInt(1_000_000_000);
                    transCase = 2;
                }
            } else  { //적금이라면
                if(endAt.isBefore(LocalDateTime.now())){ //만료되었다면
                    postMoney = kofaker.random().nextInt(1_000_000_000 / duration.getDuration());
                    money=(int) (postMoney * duration.getDuration() * (1+(duration.getEndInterest().doubleValue()+discountedRate.doubleValue())));
                    depositAccount.setCancelDate(endAt); //endAt이 현재보다 이전이라면 endAt과 같게. 아니면 null로 둠.
                    transCase = 3;
                } else {
                    bet = (int)ChronoUnit.MONTHS.between(localDateTime, LocalDateTime.now());
                    postMoney = kofaker.random().nextInt(1_000_000_000 / bet);
                    money =postMoney*bet;
                    transCase = 4;
                }
            }


            account.setAccountNum("50403"+kofaker.numerify("########"));
            account.setEmployeeId(employees.get(kofaker.random().nextInt(employees.size())));
            account.setCustId(kofaker.random().nextLong(10001)+1);
            account.setProductId(demandDeposit.getDepositId());
            account.setAccountPassword(kofaker.numerify("####"));
            account.setStatusCode("00201");
            account.setCreatedAt(localDateTime);


            depositAccount.setAccountId(account.getAccountId());
            depositAccount.setDepositDurationId(duration.getDepositDurationId());
            depositAccount.setEndAt(endAt);
            account.setMoney(money);
            depositAccount.setDiscountedRate(discountedRate);

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
            depositMapper.saveDepositAccount(depositAccount);

            //입출금내역 입력
            switch (transCase) {
                //예금만료
                case 1: {
                    TransactionHistory history1 = new TransactionHistory();
                    history1.setAccountId(account.getAccountId());
                    history1.setTransactionFeeId(selTranFeeId.get(kofaker.random().nextInt(selTranFeeId.size())));
                    history1.setFlag(1);
                    history1.setMoney((long) postMoney);
                    history1.setToName("예금 입금");
                    history1.setAccountNum(""); //내부계좌 중 하나로 수정할것
                    history1.setToBankCode("300");
                    history1.setCreatedAt(localDateTime);
                    history1.setLocation(kofaker.address().fullAddress());
                    history1.setHsMoney((long) postMoney);
                    history1.setAtmCode("0030" + (2 + random.nextInt(2)));

                    TransactionHistory history2 = new TransactionHistory();
                    history2.setAccountId(account.getAccountId());
                    history2.setTransactionFeeId(history1.getTransactionFeeId());
                    history2.setFlag(1);
                    history2.setMoney((long) (money - postMoney));
                    history2.setToName("예금 만료");
                    history2.setAccountNum(""); //내부계좌 중 하나로 수정할것
                    history2.setToBankCode("300");
                    history2.setCreatedAt(endAt);
                    history2.setLocation(history1.getLocation());
                    history2.setHsMoney((long) money);
                    history2.setAtmCode("0030" + (2 + random.nextInt(2)));

                    histories.add(history1);
                    histories.add(history2);
                    break;
                }
                //예금만료전
                case 2: {
                    TransactionHistory history = new TransactionHistory();
                    history.setAccountId(account.getAccountId());
                    history.setTransactionFeeId(selTranFeeId.get(kofaker.random().nextInt(selTranFeeId.size())));
                    history.setFlag(1);
                    history.setMoney((long) postMoney);
                    history.setToName("예금 입금");
                    history.setAccountNum(""); //내부계좌 중 하나로 수정할것
                    history.setToBankCode("300");
                    history.setCreatedAt(localDateTime);
                    history.setLocation(kofaker.address().fullAddress());
                    history.setHsMoney((long) postMoney);
                    history.setAtmCode("0030" + (2 + random.nextInt(2)));
                    histories.add(history);
                    break;
                }
                //적금만료
                case 3: {
                    long feeId=selTranFeeId.get(kofaker.random().nextInt(selTranFeeId.size()));
                    long hsMoney=0;
                    for (int j = 0; j < duration.getDuration(); j++) {
                        TransactionHistory history = new TransactionHistory();
                        history.setAccountId(account.getAccountId());
                        history.setTransactionFeeId(feeId);
                        history.setFlag(1);
                        history.setMoney((long) postMoney);
                        history.setToName("적금 "+(j+1)+"회차");
                        history.setAccountNum(""); //내부계좌 중 하나로 수정할것
                        history.setToBankCode("300");
                        history.setCreatedAt(localDateTime);
                        history.setLocation(kofaker.address().fullAddress());
                        history.setHsMoney(hsMoney + (long) postMoney);
                        history.setAtmCode("0030" + (2 + random.nextInt(2)));
                        histories.add(history);
                    }

                    histories.get(histories.size()-1).setMoney((long)money-hsMoney+postMoney);
                    histories.get(histories.size()-1).setHsMoney((long)money);

                    break;
                }
                //적금만료전
                case 4: {
                    long feeId=selTranFeeId.get(kofaker.random().nextInt(selTranFeeId.size()));
                    long hsMoney=0;
                    for (int j = 0; j < bet; j++){
                        TransactionHistory history = new TransactionHistory();
                        history.setAccountId(account.getAccountId());
                        history.setTransactionFeeId(feeId);
                        history.setFlag(1);
                        history.setMoney((long) postMoney);
                        history.setToName("적금 "+(j+1)+"회차");
                        history.setAccountNum(""); //내부계좌 중 하나로 수정할것
                        history.setToBankCode("300");
                        history.setCreatedAt(localDateTime);
                        history.setLocation(kofaker.address().fullAddress());
                        history.setHsMoney(hsMoney + (long) postMoney);
                        history.setAtmCode("0030" + (2 + random.nextInt(2)));
                        histories.add(history);
                    }
                    break;
                }
                default: {
                    break;
                }
            }

            localDateTime = localDateTime.plusDays(kofaker.random().nextInt(14)).plusMinutes(kofaker.random().nextInt(59))
                    .plusHours(kofaker.random().nextInt(23));
        }

        histories=histories.stream().sorted(Comparator.comparing(TransactionHistory::getCreatedAt)).collect(Collectors.toList());

        historyMapper.saveHistories(histories);

    }
}
