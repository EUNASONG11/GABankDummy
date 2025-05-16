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
    void insertLoanRateLogs() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        int cnt = 0;
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

                for (int i = 0; i < count; i++) { // 1~10 랜덤 for문
                    BigDecimal baseRate = BigDecimal.valueOf(kofaker.random().nextDouble(2.8, 4.9));
                    BigDecimal interestRate = baseRate.add(addRate).subtract(discountRate);

                    LoanRateLog log = new LoanRateLog();
                    log.setAccountId(accountId);
                    log.setInterestRate(interestRate);
                    log.setChangeAt(LocalDate.of(9999, 1, 1));
                    // created_at은 나중에 정함
                    // 랜덤 돌린만큼 LoanRateLog 객체 생성
                    allLogs.add(log);
                }
            }

            // 섞기 (account_id가 완전 무작위로 분산되게)
            Collections.shuffle(allLogs);

            // 생성 시점 범위 설정
            LocalDate startDate = LocalDate.of(2022, 1, 1);
            LocalDate endDate = LocalDate.now(); // 오늘 날짜 (2025-05-08)

            // account_id별 등장 횟수 누적용
            Map<Long, Integer> accountOrder = new HashMap<>();

            // account_id별 최대 count 저장 (accountId → count)
            Map<Long, Integer> accountMaxCount = new HashMap<>();
            for (LoanRateLog log : allLogs) {
                accountMaxCount.put(log.getAccountId(), accountMaxCount.getOrDefault(log.getAccountId(), 0) + 1);
            }

            for (int i = 0; i < allLogs.size() && i < totalLogs; i++) {
                LoanRateLog log = allLogs.get(i);
                Long accountId = log.getAccountId();

                int order = accountOrder.getOrDefault(accountId, 0);
                int maxOrder = accountMaxCount.get(accountId);

                // account별 날짜 범위 내에서 order 비율에 따라 날짜 계산
                long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
                long step = (long) ((order / (double) maxOrder) * totalDays);

                log.setCreatedAt(startDate.plusDays(step));
                accountOrder.put(accountId, order + 1);
                cnt++;
                loanMapper.insLoanRateLog(log);
                if(cnt %200 ==0){
                    sqlSession.flushStatements();
                }
            }

            sqlSession.flushStatements();
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            throw e;
        } finally {
            sqlSession.close();
        }
    }
}