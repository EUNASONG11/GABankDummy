package com.example.projectdummy.exchange;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.exchange.model.ExchangeLog;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

public class ExchangeLogDummy extends DummyDefault {
    @Autowired ExchangeMapper exchangeMapper;

    @Test
    void insertExchangeLog() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        List<String> selCurrency = exchangeMapper.selCurrency();
        Random random = new Random();

        int totalLog = 100000;

        // 생성 시점 범위 설정
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.now(); // 오늘 날짜

        for (int i = 0; i < totalLog; i++) {
            String currency = selCurrency.get(random.nextInt(selCurrency.size()));

            ExchangeLog exchangeLog = new ExchangeLog();
            exchangeLog.setCurrency(currency);

            // 랜덤 createdAt 날짜 생성 (날짜 + 시간)
            long startEpochDay = startDate.toEpochDay();
            long endEpochDay = endDate.toEpochDay();
            long randomEpochDay = startEpochDay + (long) (random.nextDouble() * (endEpochDay - startEpochDay));
            LocalDate randomDate = LocalDate.ofEpochDay(randomEpochDay);

            // 랜덤 시간 생성: 0~23시간, 0~59분, 0~59초
            int randomHour = random.nextInt(24);
            int randomMinute = random.nextInt(60);
            int randomSecond = random.nextInt(60);

            // 랜덤으로 날짜와 시간을 결합하여 LocalDateTime 생성
            LocalTime randomTime = LocalTime.of(randomHour, randomMinute, randomSecond);
            LocalDateTime randomDateTime = LocalDateTime.of(randomDate, randomTime);

            exchangeLog.setCreatedAt(randomDateTime); // 랜덤 날짜 + 시간 설정

            // 통화별 값 설정
            if(currency.equals("USD")) {
                double randomValue = 1400 + (random.nextDouble() * 100); // 1400 ~ 1500 사이 실수
                BigDecimal USD = BigDecimal.valueOf(randomValue).setScale(2, RoundingMode.HALF_UP); // 소수점 둘째 자리까지 반올림

                exchangeLog.setStandardMoney(USD);
                exchangeLog.setChangeRate(BigDecimal.valueOf(1.75));
            }

            if(currency.equals("JPY")) {
                double randomValue = 950 + (random.nextDouble() * 40); // 950 ~ 990 사이 실수
                BigDecimal JPY = BigDecimal.valueOf(randomValue).setScale(2, RoundingMode.HALF_UP); // 소수점 둘째 자리까지 반올림

                exchangeLog.setStandardMoney(JPY);
                exchangeLog.setChangeRate(BigDecimal.valueOf(1.75));
            }

            if(currency.equals("EUR")) {
                double randomValue = 1500 + (random.nextDouble() * 100); // 1500 ~ 1600 사이 실수
                BigDecimal EUR = BigDecimal.valueOf(randomValue).setScale(2, RoundingMode.HALF_UP); // 소수점 둘째 자리까지 반올림

                exchangeLog.setStandardMoney(EUR);
                exchangeLog.setChangeRate(BigDecimal.valueOf(1.97));
            }

            if(currency.equals("CNY")) {
                double randomValue = 170 + (random.nextDouble() * 30); // 170 ~ 200 사이 실수
                BigDecimal CNY = BigDecimal.valueOf(randomValue).setScale(2, RoundingMode.HALF_UP); // 소수점 둘째 자리까지 반올림

                exchangeLog.setStandardMoney(CNY);
                exchangeLog.setChangeRate(BigDecimal.valueOf(5.00));
            }

            // MyBatis insert 호출
            exchangeMapper.insExchangeLog(exchangeLog); // insert 호출 (배치 방식 처리 필요)
        }

        sqlSession.commit(); // 배치 커밋
        sqlSession.close();
    }
}
