package com.example.projectdummy.loan;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.account.AccountMapper;
import com.example.projectdummy.loan.model.LoanAccount;
import com.example.projectdummy.loan.model.LoanInfo;
import com.example.projectdummy.loan.model.LoanOverdue;
import com.example.projectdummy.loan.model.LoanRepayment;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class LoanOverdueDummy extends DummyDefault {
    @Autowired
    LoanMapper loanMapper;
    final Long pk = 20000000L;
    @Autowired
    AccountMapper accountMapper;
    final int cnt = 100;
    final Long minDiscount = 50000000L;

    @Test
    void loanOverdue() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        int cnt = loanMapper.selLoanAccountCnt();
        int cnt2 = (int) (cnt * 0.05);
        for (int i = 0; i < cnt2; i++) {
            int ran = (int) (Math.random() * cnt + 1);
            int day = (int) (Math.random() * 31 + 1);
            LoanAccount loanAccount = loanMapper.selLoanAccount(pk + ran);
            LoanRepayment loanRepayment = loanMapper.selLoanRepayment(loanAccount.getEndAt(), pk + ran);
            if (loanRepayment == null) {
                i--;
                continue;
            }
            LocalDate month = loanRepayment.getDueAt().minusMonths((int) (Math.random() * 21 + 14));
            if (month.isAfter(LocalDate.now().minusMonths(1))) {
                i--;
                continue;
            }
            LoanRepayment loanRepaymentBefore = loanMapper.selLoanRepayment(month, pk + ran);
            LoanRepayment loanRepaymentAfter = loanMapper.selLoanRepayment2(loanRepaymentBefore.getLoanRepaymentId() + 1);
            loanMapper.updLoanRepaymentDueCode(loanRepaymentBefore.getLoanRepaymentId());
            LoanOverdue loanOverdue = new LoanOverdue();
            loanOverdue.setLoanRepaymentId(loanRepaymentBefore.getLoanRepaymentId());
            loanOverdue.setOverdueMoney(loanRepaymentBefore.getTotalDue());
            loanOverdue.setPaymentFlag(0);
            loanOverdue.setCreatedAt(loanRepaymentBefore.getDueAt());
            LocalDate ed = loanRepaymentBefore.getDueAt().plusDays(day);
            loanOverdue.setUpdatedAt(ed);
            LocalDate startDate = loanRepaymentBefore.getDueAt();
            LocalDate endDate = loanRepaymentAfter.getDueAt();
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            long interest = (long) (loanRepaymentBefore.getTotalDue() * 0.03 / 365) * day;
            loanOverdue.setOverdueInterest(interest);
            loanMapper.insLoanOverdue(loanOverdue);
            if (totalDays <= day) {
                long totalDue = loanRepaymentAfter.getTotalDue() + loanRepaymentBefore.getTotalDue() + interest;
                long principal = loanRepaymentAfter.getPrincipal() + loanRepaymentBefore.getPrincipal();
                long interest2 = loanRepaymentBefore.getInterest() + loanRepaymentAfter.getInterest() + interest;
                loanMapper.updLoanRepayment(totalDue, principal, interest2, loanRepaymentAfter.getLoanRepaymentId(), ed);
            }
        }
    }
}

