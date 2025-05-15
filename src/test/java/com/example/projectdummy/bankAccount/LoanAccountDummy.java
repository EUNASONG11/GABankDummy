package com.example.projectdummy.bankAccount;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.account.AccountMapper;
import com.example.projectdummy.account.model.BankAccount;
import com.example.projectdummy.account.model.ContractDocument;
import com.example.projectdummy.customer.CustomerMapper;
import com.example.projectdummy.employee.EmployeeMapper;
import com.example.projectdummy.loan.LoanMapper;
import com.example.projectdummy.loan.model.Loan;
import com.example.projectdummy.loan.model.LoanAccount;
import com.example.projectdummy.loan.model.LoanApplication;
import com.example.projectdummy.productAndDeposit.ProductMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

public class LoanAccountDummy extends DummyDefault {
    @Autowired
    LoanMapper loanMapper;
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    ProductMapper productMapper;
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

        // 대출신청이력 테이블 추가로 인해
        // 대출계좌 insert 전 대출신청이력에 상태 01902로 된 대출신청이력이 먼저 들어가 있어야 함

        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.now();
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        String[] bankDummy= {"356", "1002", "012", "00"};
        Long finalId = accountMapper.selFinalPk();
        Long accountId = finalId == null ? 10000001L : finalId+1;
        Long loanApplicationId = 1L;
        Long custId=0L;
        for(Loan loan : selLoan){
            Long loanId = loan.getLoanId();
            List<Long> selPD = productMapper.selProductDocument(loanId);
            for(int j=0;j<cnt;j++){
                long randomDays = random.nextInt((int) totalDays + 1);
                LocalDate randomDate = startDate.plusDays(randomDays);
                int randomHour = kofaker.random().nextInt(24);
                int randomMinute = kofaker.random().nextInt(60);

                LocalDateTime rd = randomDate.atTime(randomHour, randomMinute);
                LocalDateTime ad = rd.minusDays(kofaker.random().nextInt(5));

                // 지금은 일자 고정인데 시간까지 필요

                int yearsToAdd = kofaker.random().nextInt(3) + 3;
                LocalDate finalDate = randomDate.plusYears(yearsToAdd);

                int loanAmount = kofaker.random().nextInt(200_000_001) + 200_000_000;
                int loanMoney = kofaker.random().nextInt(50_000_000) + loanAmount - 50_000_000;
                custId += 1+(kofaker.random().nextLong(11));
                if(custId > 10000){
                    break;
                }
                for(Long pdi :  selPD){
                    ContractDocument cd = ContractDocument.builder()
                            .productDocumentId(pdi)
                            .contractId(loanApplicationId)
                            .document("loanDocument"+loanApplicationId)
                            .createdAt(rd)
                            .productCode("00403")
                            .build();
                    productMapper.insContractDocument(cd);
                }

                LoanApplication la = new  LoanApplication();
                la.setLoanApplicationId(loanApplicationId);
                la.setCustId(custId);
                la.setLoanId(loanId);
                la.setApplicationDate(ad);
                la.setRequestedTerm(yearsToAdd);
                la.setRequestedAmount(loanMoney);
                la.setStatusCode("01902");
                la.setDecisionDate(rd);
                loanMapper.insLoanApplication(la);


                BankAccount ba = new BankAccount();
                ba.setAccountId(accountId);
                ba.setProductId(loanId);
                ba.setCustId(custId);
                ba.setEmployeeId(selEmployee.get(kofaker.random().nextInt(selEmployee.size())));
                ba.setAccountNum("50402"+kofaker.numerify("########"));
                ba.setAccountPassword(kofaker.numerify("####"));
                ba.setMoney(loanMoney); //최저값이 없음 수정 필요
                ba.setStatusCode("00201");
                ba.setCreatedAt(rd);
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
                loanAccount.setLoanApplicationId(loanApplicationId++);
                String bank = bankDummy[random.nextInt(bankDummy.length)];
                String newBankAccount;
                String bankCode;
                if(bank.equals("00")){
                    newBankAccount = kofaker.numerify("##############");
                    bankCode = "004";
                } else if(bank.equals("012")){
                    newBankAccount = kofaker.numerify("012########");
                    bankCode = "003";
                } else if(bank.equals("356")){
                    newBankAccount = kofaker.numerify("356##########");
                    bankCode = "011";
                } else {
                    newBankAccount = kofaker.numerify("1002#########");
                    bankCode = "020";
                }

                loanAccount.setBankCode(bankCode);
                loanAccount.setUseAccount(newBankAccount);


                accountMapper.insLoanAccount(loanAccount);

            }
            sqlSession.flushStatements();
        }
    }
}
