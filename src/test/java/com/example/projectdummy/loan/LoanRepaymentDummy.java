package com.example.projectdummy.loan;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.account.AccountMapper;
import com.example.projectdummy.account.model.BankAccount;
import com.example.projectdummy.customer.CustomerMapper;
import com.example.projectdummy.employee.EmployeeMapper;
import com.example.projectdummy.loan.model.Loan;
import com.example.projectdummy.loan.model.LoanAccount;
import com.example.projectdummy.loan.model.LoanInfo;
import com.example.projectdummy.loan.model.LoanRepayment;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

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
                for (int j = 1; j <= totalMonths; j++) {
                    LoanRepayment loanRepayment = new LoanRepayment();
                    loanRepayment.setAccountId(loanInfo.getAccountId());
                    LocalDate month = loanInfo.getCreatedAt().plusMonths(j);
                    loanRepayment.setFinalAt(LocalDate.parse("9999-12-31"));
                    if (j == totalMonths && month.isBefore(LocalDate.now())) {
                        loanRepayment.setFinalAt(month);
                    }
                    if (month.isBefore(LocalDate.now())) {
                        loanRepayment.setDueCode("01202");
                    } else {
                        loanRepayment.setDueCode("01201");
                    }
                    long interest = (long) (remainingPrincipal * monthlyRate);
                    long principalPayment = monthlyPayment - interest;
                    remainingPrincipal -= principalPayment;
                    loanRepayment.setTotalDue(monthlyPayment);
                    loanRepayment.setDueAt(month);
                    loanRepayment.setPrincipal(principalPayment);
                    loanRepayment.setInterest(interest);
                    loanMapper.insLoanRepayment(loanRepayment);
                }
            } else if (loanInfo.getRedemptionCode().equals("01103")) {
                Double annualRate = Double.valueOf(kofaker.random().nextDouble(0.028, 0.049));
                Double monthlyRate = annualRate / 12.0;
                long monthlyPrincipal = (long) (loanInfo.getLoanMoney() / totalMonths);
                double principal = loanInfo.getLoanMoney();
                long remainingPrincipal = (long) principal;
                for (int j = 1; j <= totalMonths; j++) {
                    LoanRepayment loanRepayment = new LoanRepayment();
                    loanRepayment.setAccountId(loanInfo.getAccountId());
                    LocalDate month = loanInfo.getCreatedAt().plusMonths(j);
                    loanRepayment.setFinalAt(LocalDate.parse("9999-12-31"));
                    if (j == totalMonths && month.isBefore(LocalDate.now())) {
                        loanRepayment.setFinalAt(month);
                    }
                    if (month.isBefore(LocalDate.now())) {
                        loanRepayment.setDueCode("01202");
                    } else {
                        loanRepayment.setDueCode("01201");
                    }
                    long interest = (long) (remainingPrincipal * monthlyRate);
                    long monthlyPayment = monthlyPrincipal + interest;
                    remainingPrincipal -= monthlyPrincipal;
                    loanRepayment.setTotalDue(monthlyPayment);
                    loanRepayment.setDueAt(month);
                    loanRepayment.setPrincipal(monthlyPrincipal);
                    loanRepayment.setInterest(interest);
                    loanMapper.insLoanRepayment(loanRepayment);
                }

            }

        }

    }
}
