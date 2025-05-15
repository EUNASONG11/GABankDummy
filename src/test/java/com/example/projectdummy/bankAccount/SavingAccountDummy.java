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

        List<SavingsDeposit> savingDeposits = depositMapper.selSavingDeposit();
        List<Long> employees = employeeMapper.selEmployee();
        List<DepositDuration> durations= depositMapper.selDepositDuration();
        Random random = new Random();
        LocalDateTime localDateTime = LocalDateTime.of(2012, 1, 1, 0, 1);
        List<TransactionHistory> histories = new ArrayList<>();


        for(int i = 0 ; i < CNT ; i++) {
            if(localDateTime.isAfter(LocalDateTime.now())){
                break;
            }
            SavingsDeposit demandDeposit = savingDeposits.get(random.nextInt(savingDeposits.size()));
            DepositDuration duration = durations.get(random.nextInt(durations.size()));
            boolean success = false;
            int retryCnt = 0;
            BankAccount account = new BankAccount();
            DepositAccount depositAccount=new DepositAccount();

            // deposit_account의 우대금리(discountedRate): 예금 테이블의 우대금리 이하의 랜덤 값.
            // 정기예금인지 적금인지에 따라 입출금 기록도 필요할듯?
            double discount=random.nextDouble()*demandDeposit.getDiscountedRate().doubleValue();
            BigDecimal discountedRate = new BigDecimal(discount).setScale(1, RoundingMode.DOWN);
            double interestRate = discount + duration.getEndInterest().doubleValue(); //총 이윤

            LocalDateTime endAt=localDateTime.plusMonths(duration.getDuration());
            int postMoney = kofaker.random().nextInt(1_000_000_000); //원금
            int money; //이윤붙은 금액
            int bet = (int)ChronoUnit.MONTHS.between(localDateTime, LocalDateTime.now()) >0 ?
                    (int)ChronoUnit.MONTHS.between(localDateTime, LocalDateTime.now()):1; //가입기간
            int transCase = 0;
            if(demandDeposit.getDepositCode().equals("00503")) { //정기예금이라면
                if (endAt.isBefore(LocalDateTime.now())) { //만료되었다면
                    money = (int) (postMoney * (interestRate+1));
                    depositAccount.setCancelDate(endAt); //endAt이 현재보다 이전이라면 endAt과 같게. 아니면 null로 둠.
                    transCase = 1;
                } else {
                    money = postMoney;
                    transCase = 2;
                }
            } else  { //적금이라면
                if(endAt.isBefore(LocalDateTime.now())){ //만료되었다면
                    postMoney=postMoney/ duration.getDuration() ;
                    money=(int) (postMoney * duration.getDuration() * (1+(interestRate)));
                    depositAccount.setCancelDate(endAt); //endAt이 현재보다 이전이라면 endAt과 같게. 아니면 null로 둠.
                    transCase = 3;
                } else {
                    postMoney = postMoney/bet;
                    money =postMoney*bet;
                    transCase = 4;
                }
            }


            account.setAccountNum("50403"+kofaker.numerify("########"));
            account.setEmployeeId(employees.get(kofaker.random().nextInt(employees.size())));
            account.setCustId(kofaker.random().nextLong(10000)+1);
            account.setProductId(demandDeposit.getDepositId());
            account.setAccountPassword(kofaker.numerify("####"));
            account.setStatusCode("00201");
            account.setCreatedAt(localDateTime);

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
            depositAccount.setAccountId(account.getAccountId());
            depositMapper.saveDepositAccount(depositAccount);

            savingContractDocument(account.getProductId(), account.getAccountId(), "00401",localDateTime);


            //입출금내역 입력
            switch (transCase) {
                //예금만료
                case 1: {
                    TransactionHistory history1 = new TransactionHistory();
                    String location = kofaker.address().fullAddress();
                    history1.setAccountId(account.getAccountId());
                    history1.setTransactionFeeId(17L);
                    history1.setFlag(1);
                    history1.setMoney((long) postMoney);
                    history1.setToName("예금 입금");
                    history1.setAccountNum(kofaker.numerify("############"));
                    history1.setToBankCode(kofaker.numerify("00#"));
                    history1.setCreatedAt(localDateTime);
                    history1.setLocation(location);
                    history1.setHsMoney((long) postMoney);
                    history1.setAtmCode("0030" + (2 + random.nextInt(2)));

                    TransactionHistory history2 = new TransactionHistory();
                    history2.setAccountId(account.getAccountId());
                    history2.setTransactionFeeId(history1.getTransactionFeeId());
                    history2.setFlag(1);
                    history2.setMoney((long) (money - postMoney));
                    history2.setToName("예금 만료");
                    history2.setAccountNum(history1.getAccountNum());
                    history2.setToBankCode(history1.getToBankCode());
                    history2.setCreatedAt(endAt);
                    history2.setLocation(location);
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
                    history.setTransactionFeeId(17L);
                    history.setFlag(1);
                    history.setMoney((long) postMoney);
                    history.setToName("예금 입금");
                    history.setAccountNum(kofaker.numerify("############"));
                    history.setToBankCode(kofaker.numerify("00#"));
                    history.setCreatedAt(localDateTime);
                    history.setLocation(kofaker.address().fullAddress());
                    history.setHsMoney((long) postMoney);
                    history.setAtmCode("0030" + (2 + random.nextInt(2)));
                    histories.add(history);
                    break;
                }
                //적금만료
                case 3: {
                    long hsMoney=0;
                    String accNum=kofaker.numerify("############");
                    String accCode=kofaker.numerify("00#");
                    String location = kofaker.address().fullAddress();
                    for (int j = 0; j < duration.getDuration(); j++) {
                        TransactionHistory history = new TransactionHistory();
                        history.setAccountId(account.getAccountId());
                        history.setTransactionFeeId(17L);
                        history.setFlag(1);
                        history.setMoney((long) postMoney);
                        history.setToName("적금 "+(j+1)+"회차");
                        history.setAccountNum(accNum);
                        history.setToBankCode(accCode);
                        history.setCreatedAt(localDateTime.plusMonths(j));
                        history.setLocation(location);
                        history.setHsMoney(hsMoney + (long) postMoney);
                        history.setAtmCode("0030" + (2 + random.nextInt(2)));
                        histories.add(history);
                        hsMoney+=postMoney;
                    }

                    histories.get(histories.size()-1).setMoney((long)money-hsMoney+postMoney);
                    histories.get(histories.size()-1).setHsMoney((long)money);

                    break;
                }
                //적금만료전
                case 4: {
                    long hsMoney=0;
                    String accNum=kofaker.numerify("############");
                    String accCode=kofaker.numerify("00#");
                    String location = kofaker.address().fullAddress();
                    for (int j = 0; j < bet; j++){
                        TransactionHistory history = new TransactionHistory();
                        history.setAccountId(account.getAccountId());
                        history.setTransactionFeeId(17L);
                        history.setFlag(1);
                        history.setMoney((long) postMoney);
                        history.setToName("적금 "+(j+1)+"회차");
                        history.setAccountNum(accNum);
                        history.setToBankCode(accCode);
                        history.setCreatedAt(localDateTime.plusMonths(j));
                        history.setLocation(location);
                        history.setHsMoney(hsMoney + (long) postMoney);
                        history.setAtmCode("0030" + (2 + random.nextInt(2)));
                        histories.add(history);
                        hsMoney+=postMoney;
                    }
                    break;
                }
                default: {
                    break;
                }
            }

            localDateTime = localDateTime.plusMinutes(kofaker.random().nextInt(59))
                    .plusHours(kofaker.random().nextInt(23));
        }

        histories=histories.stream().sorted(Comparator.comparing(TransactionHistory::getCreatedAt)).collect(Collectors.toList());

        int totalSize = histories.size();
        int quarter = totalSize / 6;

        List<TransactionHistory> part1 = histories.subList(0, quarter);
        List<TransactionHistory> part2 = histories.subList(quarter, quarter * 2);
        List<TransactionHistory> part3 = histories.subList(quarter * 2, quarter * 3);
        List<TransactionHistory> part4 = histories.subList(quarter * 3, quarter * 4);
        List<TransactionHistory> part5 = histories.subList(quarter * 4, quarter * 5);
        List<TransactionHistory> part6 = histories.subList(quarter * 5, totalSize);

        historyMapper.saveHistories(part1);
        historyMapper.saveHistories(part2);
        historyMapper.saveHistories(part3);
        historyMapper.saveHistories(part4);
        historyMapper.saveHistories(part5);
        historyMapper.saveHistories(part6);

    }
}
