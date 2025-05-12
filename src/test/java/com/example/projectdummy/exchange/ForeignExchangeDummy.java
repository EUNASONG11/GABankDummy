package com.example.projectdummy.exchange;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.exchange.model.ExchangeLog;
import com.example.projectdummy.exchange.model.ForeignExchange;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ForeignExchangeDummy extends DummyDefault {
    @Autowired
    ExchangeMapper exchangeMapper;

    @Test
    void foreignExchange() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        List<ExchangeLog> selExchangeLog = exchangeMapper.selExchangeLogWithCreatedAt();
        List<Long> selBankAccount = exchangeMapper.selBankAccount();
        List<Long> selEmployee = exchangeMapper.selEmployee();
        Random random = new Random();

        int totalLog = Math.min(100_000, selExchangeLog.size()); // exchange_log 수보다 많이 못 넣음

        // Shuffle해서 중복 없이 사용할 준비
        Collections.shuffle(selExchangeLog);

        for (int i = 0; i < totalLog; i++) {
            ForeignExchange foreignExchange = new ForeignExchange();

            int chance = random.nextInt(100);
            if (chance < 20 || selEmployee.isEmpty()) {
                foreignExchange.setEmployeeId(null);
            } else {
                foreignExchange.setEmployeeId(selEmployee.get(random.nextInt(selEmployee.size())));
            }

            foreignExchange.setAccountId(selBankAccount.get(random.nextInt(selBankAccount.size())));

            ExchangeLog log = selExchangeLog.get(i); // 중복 없이 한 번씩만 사용
            foreignExchange.setExchangeLogId(log.getExchangeLogId());
            foreignExchange.setCreatedAt(log.getCreatedAt());

            int changeFlag = random.nextInt(2);
            foreignExchange.setChangeFlag(changeFlag);

            BigDecimal standardMoney = log.getStandardMoney();
            BigDecimal chargeRate = log.getChargeRate();

            if (standardMoney == null || chargeRate == null) {
                System.out.println("⚠️ 누락된 환율 데이터: logId=" + log.getExchangeLogId());
                continue; // 건너뜀
            }

            BigDecimal rate = BigDecimal.ONE.subtract(chargeRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));

            if (changeFlag == 0) {
                // 원화 → 외화
                long fromExchange = random.nextInt(500_000) + 500_000;
                BigDecimal from = BigDecimal.valueOf(fromExchange);
                BigDecimal to = from.divide(standardMoney, 10, RoundingMode.HALF_UP).multiply(rate);

                foreignExchange.setFromExchange(fromExchange);
                foreignExchange.setToExchange(to.longValue());
            } else {
                // 외화 → 원화
                long fromExchange = random.nextInt(500) + 100;
                BigDecimal from = BigDecimal.valueOf(fromExchange);
                BigDecimal to = from.multiply(standardMoney).multiply(rate);

                foreignExchange.setFromExchange(fromExchange);
                foreignExchange.setToExchange(to.longValue());
            }

            exchangeMapper.insForeignExchange(foreignExchange);
        }

        sqlSession.commit();
        sqlSession.close();
    }
}