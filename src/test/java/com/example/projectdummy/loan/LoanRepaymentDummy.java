package com.example.projectdummy.loan;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.account.AccountMapper;
import com.example.projectdummy.account.model.LoanUser;
import com.example.projectdummy.account.model.TransactionHistory;
import com.example.projectdummy.loan.model.LoanInfo;
import com.example.projectdummy.loan.model.LoanRepayment;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

public class LoanRepaymentDummy extends DummyDefault {
    @Autowired
    LoanMapper loanMapper;
    @Autowired
    AccountMapper accountMapper;
    final int cnt = 100;
    final Long minDiscount = 50000000L;

    @Test
    void loanRepayment() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        List<LoanInfo> selLoanInfo = loanMapper.selLoanInfo();
        for (int i = 0; i < selLoanInfo.size(); i++) {
            LoanInfo loanInfo = selLoanInfo.get(i);
            LocalDate endAt = loanInfo.getEndAt();
            LocalDate createdAt = loanInfo.getCreatedAt();
            Period period = Period.between(createdAt, endAt);
            int totalMonths = period.getYears() * 12 + period.getMonths();
            if (loanInfo.getRedemptionCode().equals("01102")) {
                Double annualRate = Double.valueOf(kofaker.random().nextDouble(0.028, 0.049));
                Double monthlyRate = annualRate / 12.0;
                double principal = loanInfo.getLoanMoney();
                double monthlyPaymentD =
                        (principal * monthlyRate * Math.pow(1 + monthlyRate, totalMonths)) /
                                (Math.pow(1 + monthlyRate, totalMonths) - 1);
                long monthlyPayment = (long) monthlyPaymentD;
                long remainingPrincipal = (long) principal;
                long totalMoneyByDue = 0;
                for (int j = 1; j <= totalMonths; j++) {
                    LoanRepayment loanRepayment = new LoanRepayment();
                    loanRepayment.setAccountId(loanInfo.getAccountId());
                    LocalDate month = loanInfo.getCreatedAt().plusMonths(j);

                    if (j == totalMonths && month.isBefore(LocalDate.now())) {
                        loanRepayment.setFinalAt(month.atStartOfDay());
                    }
                    if (month.isBefore(LocalDate.now())) {
                        loanRepayment.setDueCode("01202");
                        loanRepayment.setFinalAt(LocalDateTime.parse(month.atStartOfDay().toString()));
                    } else {
                        loanRepayment.setDueCode("01201");
                        loanRepayment.setFinalAt(LocalDateTime.parse("9999-12-31T23:59:59"));
                    }
                    long interest = (long) (remainingPrincipal * monthlyRate);
                    long principalPayment = monthlyPayment - interest;
                    remainingPrincipal -= principalPayment;
                    loanRepayment.setTotalDue(monthlyPayment);
                    loanRepayment.setDueAt(month.atStartOfDay());
                    loanRepayment.setPrincipal(principalPayment); // 원금
                    loanRepayment.setInterest(interest); // 이자
                    loanMapper.insLoanRepayment(loanRepayment);
                    totalMoneyByDue += principalPayment+interest;
                    LoanUser lu = accountMapper.selLoanUser(loanInfo.getAccountId());
                    TransactionHistory th = new TransactionHistory();
                    LocalDateTime ldt = month.atStartOfDay();
                    th.setTransactionFeeId(16L);
                    th.setAccountId(loanInfo.getAccountId());
                    th.setFlag(0);
                    th.setMoney(principalPayment+interest);
                    th.setToName("대출 납부"); // 상품에서 찾아서 넣기
                    th.setAccountNum(lu.getUseAccount());
                    th.setToBankCode(lu.getBankCode());
                    th.setCreatedAt(ldt);
                    th.setLocation((kofaker.address().fullAddress())); // 위치 어케
                    th.setHsMoney(totalMoneyByDue); //기존에 넣은거때매 +해야되는데?
                    th.setAtmCode(kofaker.random().nextInt(3)==0?"00301":"00302"); // ATM 모바일 1:2 비율

//                    th.setUseAccount(lu.getUseAccount()); // 이거 달라져도 괜찮음
//                    th.setUseBankCode(lu.getBankCode()); // 위에 따라 달라진 은행코드
                    if(!loanRepayment.getDueCode().equals("01101")){
                        accountMapper.insTranHistory(th);
                    }
                }
            } else if (loanInfo.getRedemptionCode().equals("01103")) {
                Double annualRate = Double.valueOf(kofaker.random().nextDouble(0.028, 0.049));
                Double monthlyRate = annualRate / 12.0;
                long monthlyPrincipal = (long) (loanInfo.getLoanMoney() / totalMonths);
                double principal = loanInfo.getLoanMoney();
                long remainingPrincipal = (long) principal;
                long totalMoneyByDue = 0;
                for (int j = 1; j <= totalMonths; j++) {
                    LoanRepayment loanRepayment = new LoanRepayment();
                    loanRepayment.setAccountId(loanInfo.getAccountId());
                    LocalDate month = loanInfo.getCreatedAt().plusMonths(j);
                    if (j == totalMonths && month.isBefore(LocalDate.now())) {
                        loanRepayment.setFinalAt(month.atStartOfDay());
                    }
                    if (month.isBefore(LocalDate.now())) {
                        loanRepayment.setDueCode("01202");
                        loanRepayment.setFinalAt(LocalDateTime.parse(month.atStartOfDay().toString()));
                    } else {
                        loanRepayment.setDueCode("01201");
                        loanRepayment.setFinalAt(LocalDateTime.parse("9999-12-31T23:59:59"));
                    }
                    long interest = (long) (remainingPrincipal * monthlyRate);
                    long monthlyPayment = monthlyPrincipal + interest;
                    remainingPrincipal -= monthlyPrincipal;
                    loanRepayment.setTotalDue(monthlyPayment);
                    loanRepayment.setDueAt(month.atStartOfDay());
                    loanRepayment.setPrincipal(monthlyPrincipal);
                    loanRepayment.setInterest(interest);
                    loanMapper.insLoanRepayment(loanRepayment);

                    totalMoneyByDue += monthlyPrincipal+interest;
                    LoanUser lu = accountMapper.selLoanUser(loanInfo.getAccountId());
                    TransactionHistory th = new TransactionHistory();
                    LocalDateTime ldt = month.atStartOfDay();
                    th.setTransactionFeeId(16L);
                    th.setAccountId(loanInfo.getAccountId());
                    th.setFlag(0);
                    th.setMoney(monthlyPrincipal+interest);
                    th.setToName(lu.getCustName()); // 상품에서 찾아서 넣기
                    th.setAccountNum(lu.getUseAccount());
                    th.setToBankCode(lu.getBankCode());
                    th.setCreatedAt(ldt);
                    th.setLocation("대출 납부"); // 위치 어케
                    th.setHsMoney(totalMoneyByDue); //기존에 넣은거때매 +해야되는데?
                    th.setAtmCode(kofaker.random().nextInt(3)==0?"00301":"00302"); // ATM 모바일 1:2 비율

//                    th.setUseAccount(lu.getUseAccount()); // 이거 달라져도 괜찮음
//                    th.setUseBankCode(lu.getBankCode()); // 위에 따라 달라진 은행코드
                    if(!loanRepayment.getDueCode().equals("01101")){
                        accountMapper.insTranHistory(th);
                    }
                }

            }
            sqlSession.flushStatements();
        }

    }
}
