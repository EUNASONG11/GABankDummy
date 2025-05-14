package com.example.projectdummy.bankAccount;

import com.example.projectdummy.AccountDummyDefault;
import com.example.projectdummy.account.AccountMapper;
import com.example.projectdummy.card.CardMapper;
import com.example.projectdummy.employee.EmployeeMapper;
import com.example.projectdummy.productAndDeposit.DepositMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
//        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
//
//        List<SavingsDeposit> savingDeposits = depositMapper.selSavingDeposit();
//        List<Long> employees = employeeMapper.selEmployee();
//        List<DepositDuration> durations= depositMapper.selDepositDuration();
//        Random random = new Random();
//        LocalDateTime localDateTime = LocalDateTime.of(2018, 1, 1, 0, 1);
//
//        for(int i = 0 ; i < CNT ; i++) {
//            SavingsDeposit demandDeposit = savingDeposits.get(random.nextInt(savingDeposits.size()));
//            DepositDuration duration = durations.get(random.nextInt(durations.size()));
//            boolean success = false;
//            int retryCnt = 0;
//            BankAccount account = new BankAccount();
//            DepositAccount depositAccount=new DepositAccount();
//
//            // deposit_account의 우대금리(discountedRate): 예금 테이블의 우대금리 이하의 랜덤 값.
//            // 정기예금인지 적금인지에 따라 입출금 기록도 필요할듯?
//
//            BigDecimal discountedRate = new BigDecimal(random.nextDouble()*depositAccount.getDiscountedRate()
//                    .doubleValue()).setScale(1, RoundingMode.DOWN);
//
//            int money;
//            if(demandDeposit.getDepositCode().equals("00503")){ //정기예금이라면
//                money=kofaker.random().nextInt(1_000_000_000);
//            }else { //적금이라면
//
//                TransactionHistory history = new TransactionHistory();
//
//            }
//
//
//            account.setAccountNum("50403"+kofaker.numerify("########"));
//            account.setEmployeeId(employees.get(kofaker.random().nextInt(employees.size())));
//            account.setCustId(kofaker.random().nextLong(10001)+1);
//            account.setProductId(demandDeposit.getDepositId());
//            account.setAccountPassword(kofaker.numerify("####"));
//            account.setStatusCode("00201");
//
//            LocalDateTime endAt=localDateTime.plusMonths(duration.getDuration());
//
//            depositAccount.setAccountId(account.getAccountId());
//            depositAccount.setDepositDurationId(duration.getDepositDurationId());
//            depositAccount.setEndAt(endAt);
//            if(endAt.isBefore(LocalDateTime.now())) { //endAt이 현재보다 이전이라면 endAt과 같게. 아니면 null로 둠.
//                depositAccount.setCancelDate(endAt);
//            }
//
//            account.setMoney(money);
//            depositAccount.setDiscountedRate(discountedRate);
//
//            while(!success && retryCnt < 10) {
//                try {
//                    accountMapper.saveBankAccount(account);
//                    success = true;
//                }catch (Exception e) {
//                    retryCnt++;
//                    account.setAccountNum("50403"+kofaker.numerify("########"));
//                    if (isDuplicateKeyException(e)) {
//                        System.out.println("Duplicate accountNum detected. Retrying... (" + retryCnt + ")");
//                    } else {
//                        e.printStackTrace();
//                        break;
//                    }
//                }
//            }
//            depositMapper.saveDepositAccount(depositAccount);
//
//        }



    }
}
