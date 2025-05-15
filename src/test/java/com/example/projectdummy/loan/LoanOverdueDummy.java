package com.example.projectdummy.loan;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.account.AccountMapper;
import com.example.projectdummy.customer.OverdueMapper;
import com.example.projectdummy.loan.model.*;
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
    @Autowired
    OverdueMapper overdueMapper;
    final Long pk = 10000000L;

    @Test
    void loanOverdue() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        int cnt = loanMapper.selLoanAccountCnt();
        int cnt2 = (int) (cnt * 0.05);
        for (int i = 0; i < cnt2; i++) {
            int ran = (int) (Math.random() * cnt + 1);
            int day = (int) (Math.random() * 6 + 26);
            LoanAccount loanAccount = loanMapper.selLoanAccount(pk + ran);
            LoanRepayment loanRepayment = loanMapper.selLoanRepayment(loanAccount.getEndAt().atStartOfDay(), pk + ran);
            if (loanRepayment == null) {
                i--;
                continue;
            }
            LocalDateTime month = loanRepayment.getDueAt().minusMonths((int) (Math.random() * 21 + 14));
            if (month.isAfter(LocalDateTime.now().minusMonths(1))) {
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
            loanOverdue.setUpdatedAt(loanRepaymentBefore.getDueAt().plusDays(day));
            LocalDateTime startDate = loanRepaymentBefore.getDueAt();
            LocalDateTime endDate = loanRepaymentAfter.getDueAt();
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            long interest = (long) (loanRepaymentBefore.getTotalDue() * 0.03 / 365) * day;
            loanOverdue.setOverdueInterest(interest);
            loanMapper.insLoanOverdue(loanOverdue);
            if (totalDays <= day) {
                long totalDue = loanRepaymentAfter.getTotalDue() + loanRepaymentBefore.getTotalDue() + interest;
                long principal = loanRepaymentAfter.getPrincipal() + loanRepaymentBefore.getPrincipal();
                long interest2 = loanRepaymentBefore.getInterest() + loanRepaymentAfter.getInterest() + interest;
                loanMapper.updLoanRepayment(totalDue, principal, interest2, loanRepaymentAfter.getLoanRepaymentId());
            }
            loanMapper.updLoanRepayment2(loanRepaymentBefore.getDueAt().plusDays(day), loanRepaymentBefore.getLoanRepaymentId());
            OverdueRecord overdueRecord = new OverdueRecord();
            overdueRecord.setTargetId(loanOverdue.getOverdueId());
            overdueRecord.setRemain((long)0);
            overdueRecord.setOverdueMoney(loanRepaymentBefore.getTotalDue() + interest);
            overdueRecord.setOverdueCode("01302");
            overdueRecord.setCreatedAt(loanRepaymentBefore.getDueAt().plusDays(day));
            overdueRecord.setStartAt(loanRepaymentBefore.getDueAt());
            overdueMapper.insCardOverdue(overdueRecord);
        }
    }
}

