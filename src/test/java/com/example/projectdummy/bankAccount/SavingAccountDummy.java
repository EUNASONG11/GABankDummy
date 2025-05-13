package com.example.projectdummy.bankAccount;

import com.example.projectdummy.AccountDummyDefault;
import com.example.projectdummy.account.AccountMapper;
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
import java.util.List;
import java.util.Random;

public class SavingAccountDummy extends AccountDummyDefault {
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    EmployeeMapper employeeMapper;
    @Autowired
    DepositMapper depositMapper;
    @Autowired
    CardMapper cardMapper;
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
                    int bet = (int)ChronoUnit.MONTHS.between(localDateTime, LocalDateTime.now());
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
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                default:
                    break;
            }


            localDateTime = localDateTime.plusDays(kofaker.random().nextInt(14)).plusMinutes(kofaker.random().nextInt(59))
                    .plusHours(kofaker.random().nextInt(23));
        }



    }
}
