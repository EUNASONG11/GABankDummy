package com.example.projectdummy.bankAccount;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.account.AccountMapper;
import com.example.projectdummy.account.model.BankAccount;
import com.example.projectdummy.customer.CustomerMapper;
import com.example.projectdummy.employee.EmployeeMapper;
import com.example.projectdummy.loan.LoanMapper;
import com.example.projectdummy.loan.model.Loan;
import com.example.projectdummy.loan.model.LoanAccount;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

public class LoanAccountDummy extends DummyDefault {
    @Autowired
    LoanMapper loanMapper;
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    CustomerMapper customerMapper;
    @Autowired
    EmployeeMapper employeeMapper;
    final int cnt = 100;
    final Long minDiscount = 20000000L;
    @Test
    void loanAccount() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        List<Loan> selLoan = loanMapper.selLoan2(0);
        List<Long> selLoan2 = loanMapper.selLoan(1);
        List<Long> selEmployee = employeeMapper.selEmployee();
        Random random = new Random();


        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.now();
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);


        Long accountId = 20000001L;
        for(Loan loan : selLoan){
            Long loanId = loan.getLoanId();
            for(int j=0;j<cnt;j++){
                long randomDays = random.nextInt((int) totalDays + 1);
                LocalDate randomDate = startDate.plusDays(randomDays);

                int yearsToAdd = kofaker.random().nextInt(3) + 3;
                LocalDate finalDate = randomDate.plusYears(yearsToAdd);

                int loanAmount = kofaker.random().nextInt(200_000_001) + 200_000_000;
                BankAccount ba = new BankAccount();
                ba.setAccountId(accountId);
                ba.setProductId(loanId);
                ba.setCustId(kofaker.random().nextLong(10001)+1);
                ba.setEmployeeId(selEmployee.get(kofaker.random().nextInt(selEmployee.size())));
                ba.setAccountNum("50402"+kofaker.numerify("########"));
                ba.setAccountPassword(kofaker.numerify("####"));
                ba.setMoney(kofaker.random().nextInt(loanAmount)); //최저값이 없음 수정 필요
                ba.setStatusCode("00201");
                ba.setCreatedAt(randomDate);
                accountMapper.insBankAccount(ba);

                Long maxAmount = loan.getMaximumAmount();
                Long minAmount = maxAmount - minDiscount;
                LoanAccount loanAccount = new LoanAccount();
                loanAccount.setAccountId(accountId++);
                loanAccount.setRedemptionCode(loan.getRedemptionCode());
                    loanAccount.setLoanMoney(kofaker.random().nextLong(maxAmount - minAmount + 1) + minAmount);
                loanAccount.setEndAt(finalDate);
                loanAccount.setRateId(kofaker.random().nextLong(10001)+1);
                loanAccount.setDiscountedRate(BigDecimal.valueOf(kofaker.random().nextDouble(0.1,0.5)));
                loanAccount.setAdditionalRate(BigDecimal.valueOf(kofaker.random().nextDouble(0.8,3.5)));

                accountMapper.insLoanAccount(loanAccount);

            }
            sqlSession.flushStatements();
        }
    }
}
