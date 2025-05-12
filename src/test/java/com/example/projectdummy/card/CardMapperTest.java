package com.example.projectdummy.card;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.card.model.CreditCardPayment;
import com.example.projectdummy.card.model.CreditStatement;
import net.datafaker.Faker;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class CardMapperTest extends DummyDefault {
    @Autowired
    CardMapper cardMapper;
    final Long cnt = 100000L;

    @Test
    void Generate(){
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        Faker faker = new Faker();

        //0~25% 무이자
        for(Long i=1L; i <= (cnt/4); i++){
            int langType = faker.random().nextInt(2);
            String companyName = "";
            if(langType == 0) {
                 companyName = kofaker.company().name();
                if(companyName.length() >= 30) {
                    companyName = companyName.substring(0, 29);
                }
            }else if(langType == 1) {
                companyName = enfaker.company().name();
                if(companyName.length() >= 30) {
                    companyName = companyName.substring(0, 29);
                }
            }

            int interestFree = faker.random().nextInt(5) + 2; //무이자할부기간
            int ogAmount = faker.random().nextInt(9999900) + 100; //총결제금액





            // 연도 범위 지정 (예: 2015 ~ 2025)
            int year = ThreadLocalRandom.current().nextInt(2000, 2026);

            // 월 범위 지정 (1 ~ 12)
            int month = ThreadLocalRandom.current().nextInt(1, 13);

            // 해당 년/월의 마지막 일 계산
            YearMonth yearMonth = YearMonth.of(year, month);
            int maxDay = yearMonth.lengthOfMonth(); // 윤년 자동 반영됨

            // 일 범위 지정 (1 ~ maxDay)
            int day = ThreadLocalRandom.current().nextInt(1, maxDay + 1);

            // LocalDateTime 생성 (시간 고정)
            LocalDateTime createdAt = LocalDate.of(year, month, day).atStartOfDay();

            // 문자열로 추출
            String uYear = String.valueOf(year);
            String uMonth = String.format("%02d", month);
            String uDay = String.format("%02d", day);



            BigDecimal discount = BigDecimal.valueOf(2.0 + faker.random().nextDouble() * 7.9)
                    .setScale(1, RoundingMode.HALF_UP); //할인율



            CreditStatement cs = CreditStatement.builder()
                    .creditId(i)
                    .creditCardId(faker.random().nextLong(100) + 1)
                    .place(companyName)
                    .flag(1)
                    .state(faker.random().nextInt(3))
                    .exploiter(faker.random().nextInt(2)==0?"본인":"가족")
                    .accumPoint(faker.random().nextInt(300001))
                    .discount(discount)
                    .fee(0)
                    .ogAmount(ogAmount)
                    .installment(0)
                    .interestFree(interestFree)
                    .uYear(uYear)
                    .uMonth(uMonth)
                    .uDay(uDay)
                    .createdAt(createdAt)
                    .build();
            cardMapper.insCreditStatement(cs);


            //신용카드지불내역
            String dueCode = "";
            if(i < (cnt/4)/4) {
                dueCode = "01201";
            }else if((cnt/4)/4 <= i && i < (cnt/4)/2){
                dueCode = "01202";
            }else if((cnt/4)/2 <= i && i < ((cnt/4)*3)/4){
                dueCode = "01203";
            }else {
                dueCode = "01204";
            }


            // 할인 후 금액 계산
            BigDecimal discountedAmount = BigDecimal.valueOf(ogAmount)
                    .multiply(BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                    .setScale(0, RoundingMode.DOWN); // 소수점 제거 (내림)
            Long discountedAmountLong = discountedAmount.longValue(); //형변환


            if(interestFree > 0) {
                for (int j = 1; j <= interestFree; j++) {
                    int baseMonth = Integer.parseInt(uMonth);
                    baseMonth += j;
                    if (baseMonth > 12) {
                        baseMonth = 1;
                    }
                    String paddedMonth = String.format("%02d", baseMonth);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    //String dueAtString = uYear + "-" + paddedMonth + "-" + uDay + " 00:00:00";
                    LocalDateTime dueAt = createdAt.plusMonths(1); //LocalDateTime.parse(dueAtString, formatter);
                    String dueAtString = dueAt.format(formatter);

                    // 15% 확률로만 날짜를 앞당김
                    LocalDateTime paidAt = dueAt;
                    if (ThreadLocalRandom.current().nextInt(100) < 15) {  // 0~14 → true
                        int daysToSubtract = ThreadLocalRandom.current().nextInt(1, 6); // 1~5
                        paidAt = dueAt.minusDays(daysToSubtract);
                    }
                    String paidAtStr = paidAt.format(formatter);



                    CreditCardPayment ccp = CreditCardPayment.builder()
                            .creditId(i)
                            .dueCode(dueCode)
                            .cnt(interestFree)
                            .dcAmount(discountedAmountLong / interestFree)
                            .dueAt(dueAtString)
                            .paidAt(paidAtStr)
                            .build();
                    cardMapper.insCreditCardPayment(ccp);
                }
            }

        }
        sqlSession.flushStatements();

        //25~50% 할부 이자
        for(Long i = (cnt/4) + 1; i <= cnt/2; i++) {
            int langType = faker.random().nextInt(2);
            String companyName = "";
            if(langType == 0) {
                companyName = kofaker.company().name();
                if(companyName.length() >= 30) {
                    companyName = companyName.substring(0, 29);
                }
            }else if(langType == 1) {
                companyName = enfaker.company().name();
                if(companyName.length() >= 30) {
                    companyName = companyName.substring(0, 29);
                }
            }


            int installment = faker.random().nextInt(35) + 2; //유이자할부기간
            int ogAmount = faker.random().nextInt(9999900) + 100; //총결제금액
            int fee = 0;
            if(installment == 2){ //할부개월에 따른 수수료
                fee = (int) Math.round(14.5 * ogAmount);
            }else if(installment == 3){
                fee = (int) Math.round(18.2 * ogAmount);
            }else if(4 <= installment && installment <= 18){
                fee = (int) Math.round(19.7 * ogAmount);
            }else if(19 <= installment ){
                fee = (int) Math.round(19.8 * ogAmount);
            }

            BigDecimal discount = BigDecimal.valueOf(2.0 + faker.random().nextDouble() * 7.9)
                    .setScale(1, RoundingMode.HALF_UP);

            // 연도 범위 지정 (예: 2015 ~ 2025)
            int year = ThreadLocalRandom.current().nextInt(2000, 2026);

            // 월 범위 지정 (1 ~ 12)
            int month = ThreadLocalRandom.current().nextInt(1, 13);

            // 해당 년/월의 마지막 일 계산
            YearMonth yearMonth = YearMonth.of(year, month);
            int maxDay = yearMonth.lengthOfMonth(); // 윤년 자동 반영됨

            // 일 범위 지정 (1 ~ maxDay)
            int day = ThreadLocalRandom.current().nextInt(1, maxDay + 1);

            // LocalDateTime 생성 (시간 고정)
            LocalDateTime createdAt = LocalDate.of(year, month, day).atStartOfDay();

            // 문자열로 추출
            String uYear = String.valueOf(year);
            String uMonth = String.format("%02d", month);
            String uDay = String.format("%02d", day);

            CreditStatement cs = CreditStatement.builder()
                    .creditId(i)
                    .creditCardId(faker.random().nextLong(100) + 1)
                    .place(companyName)
                    .flag(1)
                    .state(faker.random().nextInt(3))
                    .exploiter(faker.random().nextInt(2)==0?"본인":"가족")
                    .accumPoint(faker.random().nextInt(300001))
                    .discount(discount)
                    .fee(fee)
                    .ogAmount(ogAmount)
                    .installment(installment)
                    .interestFree(0)
                    .uYear(uYear)
                    .uMonth(uMonth)
                    .uDay(uDay)
                    .createdAt(createdAt)
                    .build();
            cardMapper.insCreditStatement(cs);


            //신용카드지불내역
            String dueCode = "";
            if(i < (cnt/4)/4) {
                dueCode = "01201";
            }else if((cnt/4)/4 <= i && i < (cnt/4)/2){
                dueCode = "01202";
            }else if((cnt/4)/2 <= i && i < ((cnt/4)*3)/4){
                dueCode = "01203";
            }else {
                dueCode = "01204";
            }


            // 할인 후 금액 계산
            BigDecimal discountedAmount = BigDecimal.valueOf(ogAmount)
                    .multiply(BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                    .setScale(0, RoundingMode.DOWN); // 소수점 제거 (내림)
            Long discountedAmountLong = discountedAmount.longValue(); //형변환


            if(installment > 0) {
                for (int j = 1; j <= installment; j++) {
                    int baseMonth = Integer.parseInt(uMonth);
                    baseMonth += j;
                    if (baseMonth > 12) {
                        baseMonth = 1;
                    }
                    String paddedMonth = String.format("%02d", baseMonth);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    //String dueAtString = uYear + "-" + paddedMonth + "-" + uDay + " 00:00:00";
                    LocalDateTime dueAt = createdAt.plusMonths(1); //LocalDateTime.parse(dueAtString, formatter);
                    String dueAtString = dueAt.format(formatter);

                    // 15% 확률로만 날짜를 앞당김
                    LocalDateTime paidAt = dueAt;
                    if (ThreadLocalRandom.current().nextInt(100) < 15) {  // 0~14 → true
                        int daysToSubtract = ThreadLocalRandom.current().nextInt(1, 6); // 1~5
                        paidAt = dueAt.minusDays(daysToSubtract);
                    }
                    String paidAtStr = paidAt.format(formatter);

                    CreditCardPayment ccp = CreditCardPayment.builder()
                            .creditId(i)
                            .dueCode(dueCode)
                            .cnt(installment)
                            .dcAmount(discountedAmountLong / installment + fee)
                            .dueAt(dueAtString)
                            .paidAt(paidAtStr)
                            .build();
                    cardMapper.insCreditCardPayment(ccp);
                }
            }
        }
        sqlSession.flushStatements();


        //50~100% 일시불
        for(Long i = (cnt/2) + 1; i <= cnt; i++) {
            int langType = faker.random().nextInt(2);
            String companyName = "";
            if(langType == 0) {
                companyName = kofaker.company().name();
                if(companyName.length() >= 30) {
                    companyName = companyName.substring(0, 29);
                }
            }else if(langType == 1) {
                companyName = enfaker.company().name();
                if(companyName.length() >= 30) {
                    companyName = companyName.substring(0, 29);
                }
            }


            int ogAmount = faker.random().nextInt(9999900) + 100;

            BigDecimal discount = BigDecimal.valueOf(2.0 + faker.random().nextDouble() * 7.9)
                    .setScale(1, RoundingMode.HALF_UP);

            // 연도 범위 지정 (예: 2015 ~ 2025)
            int year = ThreadLocalRandom.current().nextInt(2000, 2026);

            // 월 범위 지정 (1 ~ 12)
            int month = ThreadLocalRandom.current().nextInt(1, 13);

            // 해당 년/월의 마지막 일 계산
            YearMonth yearMonth = YearMonth.of(year, month);
            int maxDay = yearMonth.lengthOfMonth(); // 윤년 자동 반영됨

            // 일 범위 지정 (1 ~ maxDay)
            int day = ThreadLocalRandom.current().nextInt(1, maxDay + 1);

            // LocalDateTime 생성 (시간 고정)
            LocalDateTime createdAt = LocalDate.of(year, month, day).atStartOfDay();

            // 문자열로 추출
            String uYear = String.valueOf(year);
            String uMonth = String.format("%02d", month);
            String uDay = String.format("%02d", day);

            CreditStatement cs = CreditStatement.builder()
                    .creditId(i)
                    .creditCardId(faker.random().nextLong(100) + 1)
                    .place(companyName)
                    .flag(0)
                    .state(faker.random().nextInt(3))
                    .exploiter(faker.random().nextInt(2)==0?"본인":"가족")
                    .accumPoint(faker.random().nextInt(300001))
                    .discount(BigDecimal.valueOf(2.0 + faker.random().nextDouble() * 7.9)
                            .setScale(1, RoundingMode.HALF_UP))
                    .fee(0)
                    .ogAmount(ogAmount)
                    .installment(0)
                    .interestFree(0)
                    .uYear(uYear)
                    .uMonth(uMonth)
                    .uDay(uDay)
                    .createdAt(createdAt)
                    .build();
            cardMapper.insCreditStatement(cs);


            //신용카드지불내역
            String dueCode = "";
            if(i < (cnt/4)/4) {
                dueCode = "01201";
            }else if((cnt/4)/4 <= i && i < (cnt/4)/2){
                dueCode = "01202";
            }else if((cnt/4)/2 <= i && i < ((cnt/4)*3)/4){
                dueCode = "01203";
            }else {
                dueCode = "01204";
            }

            // 할인 후 금액 계산
            BigDecimal discountedAmount = BigDecimal.valueOf(ogAmount)
                    .multiply(BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                    .setScale(0, RoundingMode.DOWN); // 소수점 제거 (내림)
            Long discountedAmountLong = discountedAmount.longValue(); //형변환

            int baseMonth = Integer.parseInt(uMonth);
            baseMonth += 1;
            if (baseMonth > 12) {
                baseMonth = 1;
            }
            String paddedMonth = String.format("%02d", baseMonth);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            //String dueAtString = uYear + "-" + paddedMonth + "-" + uDay + " 00:00:00";
            LocalDateTime dueAt = createdAt.plusMonths(1); //LocalDateTime.parse(dueAtString, formatter);
            String dueAtString = dueAt.format(formatter);

            // 15% 확률로만 날짜를 앞당김
            LocalDateTime paidAt = dueAt;
            if (ThreadLocalRandom.current().nextInt(100) < 15) {  // 0~14 → true
                int daysToSubtract = ThreadLocalRandom.current().nextInt(1, 6); // 1~5
                paidAt = dueAt.minusDays(daysToSubtract);
            }
            String paidAtStr = paidAt.format(formatter);


            CreditCardPayment ccp = CreditCardPayment.builder()
                    .creditId(i)
                    .dueCode(dueCode)
                    .cnt(0)
                    .dcAmount(discountedAmountLong)
                    .dueAt(dueAtString)
                    .paidAt(paidAtStr)
                    .build();
            cardMapper.insCreditCardPayment(ccp);
        }
        sqlSession.flushStatements();

    }

    @Test
    void Generate2() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        Faker faker = new Faker();

        List<Long> idList = cardMapper.selCreditCardPayment();
        List<Long> dcAmountList = cardMapper.selDcAmount();


    }
}
