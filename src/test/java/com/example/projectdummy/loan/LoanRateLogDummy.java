package com.example.projectdummy.loan;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.loan.model.LoanRateLog;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class LoanRateLogDummy extends DummyDefault {
    @Autowired
    LoanMapper loanMapper;

    @Test
    void insertLoanRateLogs_ShuffledAccounts_CreatedAtOrderedPerAccountId() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        try {
            List<Long> accountIds = loanMapper.selLoanAccountId();
            Random random = new Random();

            int totalLogs = 4000;
            int maxLogsPerAccount = 10;

            List<LoanRateLog> allLogs = new ArrayList<>();

            for (Long accountId : accountIds) {
                int count = random.nextInt(maxLogsPerAccount) + 1;

                BigDecimal addRate = BigDecimal.valueOf(loanMapper.selAdditionalRate(accountId));
                BigDecimal discountRate = BigDecimal.valueOf(loanMapper.selDiscountedRate(accountId));

                for (int i = 0; i < count; i++) {
                    BigDecimal baseRate = BigDecimal.valueOf(kofaker.random().nextDouble(2.8, 4.9));
                    BigDecimal interestRate = baseRate.add(addRate).subtract(discountRate);

                    LoanRateLog log = new LoanRateLog();
                    log.setAccountId(accountId);
                    log.setInterestRate(interestRate);
                    log.setChangeAt(LocalDate.of(9999, 1, 1));
                    // created_at은 나중에 정함
                    allLogs.add(log);
                }
            }

            // 섞기 (account_id가 완전 무작위로 분산되게)
            Collections.shuffle(allLogs);

            // account_id별 등장 횟수 누적을 위한 맵
            Map<Long, Integer> accountOrder = new HashMap<>();
            LocalDate baseDate = LocalDate.of(2022, 1, 1);

            for (int i = 0; i < allLogs.size() && i < totalLogs; i++) {
                LoanRateLog log = allLogs.get(i);
                Long accountId = log.getAccountId();

                int order = accountOrder.getOrDefault(accountId, 0);
                log.setCreatedAt(baseDate.plusDays(order * 30L));  // 같은 account일수록 점점 미래로
                accountOrder.put(accountId, order + 1);

                loanMapper.insLoanRateLog(log);
            }

            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            throw e;
        } finally {
            sqlSession.close();
        }
    }
}