package com.example.projectdummy.loan;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.loan.model.RateLog;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LoanMapperTest extends DummyDefault {
    @Autowired
    LoanMapper loanMapper;

    @Test
    void loanLog() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        List<Long> selLoan = loanMapper.selLoan(0);

        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.now();
        String[] rateNames = {
                "cofix 6개월", "cofix 잔액", "cofix 신규취급액",
                "금융채 6개월", "금융채 1년", "금융채 2년"
        };

        for (int i = 0; i < selLoan.size(); i++) {
            Set<String> selectedRateNames = new HashSet<>();
            Random random = new Random();

            for (int j = 0; j < 3; j++) {
                selectedRateNames.add(rateNames[random.nextInt(rateNames.length)]);
            }
            List<String> selectedRateNamesList = new ArrayList<>(selectedRateNames);
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusWeeks(1)) {
                for(int k=0; k<selectedRateNames.size(); k++) {
                RateLog rateLog = new RateLog();
                rateLog.setLoanId(selLoan.get(i));
                rateLog.setUseFlag(0);
                rateLog.setBaseRateName(selectedRateNamesList.get(k));
                rateLog.setCreatedAt(date);
                rateLog.setAdditionalRate(BigDecimal.valueOf(kofaker.random().nextDouble(0.8,3.5)));
                rateLog.setBaseRate(BigDecimal.valueOf(kofaker.random().nextDouble(2.8,4.9)));
                rateLog.setDiscountedRate(BigDecimal.valueOf(kofaker.random().nextDouble(0.1,0.5)));

                    loanMapper.insLoanLog(rateLog);
                }
            }
            sqlSession.flushStatements();
        }

        List<Long> selLoan1 = loanMapper.selLoan(1);

        for (int i = 0; i < selLoan1.size(); i++) {
            Set<String> selectedRateNames = new HashSet<>();
            Random random = new Random();

            for (int j = 0; j < 3; j++) {
                selectedRateNames.add(rateNames[random.nextInt(rateNames.length)]);
            }
            List<String> selectedRateNamesList = new ArrayList<>(selectedRateNames);
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusWeeks(1)) {

                for(int k=0; k<selectedRateNames.size(); k++) {
                    RateLog rateLog = new RateLog();
                    rateLog.setLoanId(selLoan1.get(i));
                    rateLog.setUseFlag(1);
                    rateLog.setBaseRateName(selectedRateNamesList.get(k));
                    rateLog.setCreatedAt(date);
                    rateLog.setAdditionalRate(BigDecimal.valueOf(kofaker.random().nextDouble(0.8,3.5)));
                    rateLog.setBaseRate(BigDecimal.valueOf(kofaker.random().nextDouble(2.8,4.9)));
                    rateLog.setDiscountedRate(BigDecimal.valueOf(kofaker.random().nextDouble(0.1,0.5)));

                    loanMapper.insLoanLog(rateLog);
                }
            }
            sqlSession.flushStatements();
        }
    }
}

