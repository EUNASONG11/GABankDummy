package com.example.projectdummy.card;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.card.model.CreditCardPayment;
import com.example.projectdummy.card.model.CreditOverdue;
import com.example.projectdummy.card.model.CreditStatement;
import com.example.projectdummy.card.model.UserCard;
import net.datafaker.Faker;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
            int ogAmount = faker.random().nextInt(4950000) + 50000; //총결제금액(할부 최저 50,000원 이상부터)


            int year = ThreadLocalRandom.current().nextInt(2000, 2026);
            int month = ThreadLocalRandom.current().nextInt(1, 13);

            // 해당 년/월의 마지막 일 계산
            YearMonth yearMonth = YearMonth.of(year, month);
            int maxDay = yearMonth.lengthOfMonth(); // 윤년 자동 반영됨

            // 일 범위 지정 (1 ~ maxDay)
            int day = ThreadLocalRandom.current().nextInt(1, maxDay + 1);

            // LocalDateTime 생성 (시간 고정)
            LocalDateTime createdAt = LocalDate.of(year, month, day).atStartOfDay();

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

            // 할인 후 금액 계산
            BigDecimal discountedAmount = BigDecimal.valueOf(ogAmount)
                    .multiply(BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                    .setScale(0, RoundingMode.DOWN); // 소수점 제거 (내림)
            Long discountedAmountLong = discountedAmount.longValue(); //형변환





                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dueAt = createdAt.plusMonths(1);
                    String dueAtString = dueAt.format(formatter);

                    // 15% 확률로만 날짜를 앞당김
                    LocalDateTime paidAt = dueAt;
                    if (ThreadLocalRandom.current().nextInt(100) < 15) {  // 0~14 → true
                        int daysToSubtract = ThreadLocalRandom.current().nextInt(1, 6); // 1~5
                        paidAt = dueAt.minusDays(daysToSubtract);
                    }
                    String paidAtStr = paidAt.format(formatter);

                    LocalDateTime ld = LocalDateTime.now();
                    if(!paidAt.isAfter(ld)) {
//                        dueCode = "01201"; // 혹은 01204 가중치줘서 1/10 확률로 넣음
                        dueCode = kofaker.random().nextInt(10)==0 ? "01204": "01202";
                        if(!paidAt.isAfter(ld) && !paidAt.isBefore(ld.minusMonths(1))) {
                            // 1달전 더미기준 2천건이 있음 >> 여기서 1/10 인 200건이 최근 연체중인걸로 나오도록 하는게 맞을듯?
                            dueCode = kofaker.random().nextInt(10)==0 ? "01203": "01202";
                        }
                    } else {
                        dueCode = "01201";
                    }

                    if(dueCode.equals("01204")) {
                        paidAt = dueAt.minusDays(kofaker.random().nextInt(7, 31));
                        paidAtStr = paidAt.format(formatter);
                    }


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
            int ogAmount = faker.random().nextInt(4950000) + 50000; //총결제금액(할부 최저 50,000원 이상부터)
            int fee = 0;
            if(installment == 2){ //할부개월에 따른 수수료
                fee = (int) Math.round(0.145 * ogAmount);
            }else if(installment == 3){
                fee = (int) Math.round(0.182 * ogAmount);
            }else if(4 <= installment && installment <= 18){
                fee = (int) Math.round(0.197 * ogAmount);
            }else if(19 <= installment ){
                fee = (int) Math.round(0.198 * ogAmount);
            }

            BigDecimal discount = BigDecimal.valueOf(2.0 + faker.random().nextDouble() * 7.9)
                    .setScale(1, RoundingMode.HALF_UP);


            int year = ThreadLocalRandom.current().nextInt(2000, 2026);
            int month = ThreadLocalRandom.current().nextInt(1, 13);

            // 해당 년/월의 마지막 일 계산
            YearMonth yearMonth = YearMonth.of(year, month);
            int maxDay = yearMonth.lengthOfMonth(); // 윤년 자동 반영됨

            // 일 범위 지정 (1 ~ maxDay)
            int day = ThreadLocalRandom.current().nextInt(1, maxDay + 1);

            // LocalDateTime 생성 (시간 고정)
            LocalDateTime createdAt = LocalDate.of(year, month, day).atStartOfDay();

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

            // 1. dueCode for문 안에서만 사용함
            // 2. 상환일이 지난 경우는 어쳐피 01201이나 01204가 들어가야함
            // 3. 상환일이 지나지 않았으면 무조건 01203임
            // 4. 상환일이 지났지만 부근일 경우에만 연체일 가능성이 있음 01203



            // 할인 후 금액 계산
            BigDecimal discountedAmount = BigDecimal.valueOf(ogAmount)
                    .multiply(BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                    .setScale(0, RoundingMode.DOWN); // 소수점 제거 (내림)
            Long discountedAmountLong = discountedAmount.longValue(); //형변환





                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dueAt = createdAt.plusMonths(1);
                    String dueAtString = dueAt.format(formatter);

                    // 15% 확률로만 날짜를 앞당김
                    LocalDateTime paidAt = dueAt;
                    if (ThreadLocalRandom.current().nextInt(100) < 15) {  // 0~14 → true
                        int daysToSubtract = ThreadLocalRandom.current().nextInt(1, 6); // 1~5
                        paidAt = dueAt.minusDays(daysToSubtract);
                    }
                    String paidAtStr = paidAt.format(formatter);

                    LocalDateTime ld = LocalDateTime.now();
                    if(!paidAt.isAfter(ld)) {
//                        dueCode = "01201"; // 혹은 01204 가중치줘서 1/10 확률로 넣음
                        dueCode = kofaker.random().nextInt(10)==0 ? "01204": "01202";
                        if(!paidAt.isAfter(ld) && !paidAt.isBefore(ld.minusMonths(1))) {
                            // 1달전 더미기준 2천건이 있음 >> 여기서 1/10 인 200건이 최근 연체중인걸로 나오도록 하는게 맞을듯?
                            dueCode = kofaker.random().nextInt(10)==0 ? "01203": "01202";
                        }
                    } else {
                        dueCode = "01201";
                    }

                    if(dueCode.equals("01204")) {
                        paidAt = dueAt.minusDays(kofaker.random().nextInt(7, 31));
                        paidAtStr = paidAt.format(formatter);
                    }

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


            int ogAmount = faker.random().nextInt(2999900) + 100;

            BigDecimal discount = BigDecimal.valueOf(2.0 + faker.random().nextDouble() * 7.9)
                    .setScale(1, RoundingMode.HALF_UP);


            int year = ThreadLocalRandom.current().nextInt(2000, 2026);
            int month = ThreadLocalRandom.current().nextInt(1, 13);

            // 해당 년/월의 마지막 일 계산
            YearMonth yearMonth = YearMonth.of(year, month);
            int maxDay = yearMonth.lengthOfMonth(); // 윤년 자동 반영되는 감사한 코드

            // 일 범위 지정 (1 ~ maxDay)
            int day = ThreadLocalRandom.current().nextInt(1, maxDay + 1);

            // LocalDateTime 생성 (시간 고정)
            LocalDateTime createdAt = LocalDate.of(year, month, day).atStartOfDay();

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

            // 할인 후 금액 계산
            BigDecimal discountedAmount = BigDecimal.valueOf(ogAmount)
                    .multiply(BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                    .setScale(0, RoundingMode.DOWN); // 소수점 제거 (내림)
            Long discountedAmountLong = discountedAmount.longValue(); //형변환


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dueAt = createdAt.plusMonths(1);
            String dueAtString = dueAt.format(formatter);

            // 15% 확률로만 날짜를 앞당김
            LocalDateTime paidAt = dueAt;
            if (ThreadLocalRandom.current().nextInt(100) < 15) {  // 0~14 → true
                int daysToSubtract = ThreadLocalRandom.current().nextInt(1, 6); // 1~5
                paidAt = dueAt.minusDays(daysToSubtract);
            }
            String paidAtStr = paidAt.format(formatter);

            LocalDateTime ld = LocalDateTime.now();
            if(!paidAt.isAfter(ld)) {
//                        dueCode = "01201"; // 혹은 01204 가중치줘서 1/10 확률로 넣음
                dueCode = kofaker.random().nextInt(10)==0 ? "01204": "01202";
                if(!paidAt.isAfter(ld) && !paidAt.isBefore(ld.minusMonths(1))) {
                    // 1달전 더미기준 2천건이 있음 >> 여기서 1/10 인 200건이 최근 연체중인걸로 나오도록 하는게 맞을듯?
                    dueCode = kofaker.random().nextInt(10)==0 ? "01203": "01202";
                }
            } else {
                dueCode = "01201";
            }

            if(dueCode.equals("01204")) {
                paidAt = dueAt.minusDays(kofaker.random().nextInt(7, 31));
                paidAtStr = paidAt.format(formatter);
            }


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

        List<Long> idList = cardMapper.selCreditCardPaymentId("01204");
            for (Long id : idList) {
                Long dcAmount = cardMapper.selDcAmount(id);

                CreditOverdue co = CreditOverdue.builder()
                        .creditPaymentId(id)
                        .overdueAmount(dcAmount)
                        .build();

                cardMapper.insCreditOverdue(co);
            }
        sqlSession.flushStatements();
        List<Long> idList2 = cardMapper.selCreditCardPaymentId("01203");
        for (Long id : idList2) {
            Long dcAmount = cardMapper.selDcAmount(id);

            CreditOverdue co = CreditOverdue.builder()
                    .creditPaymentId(id)
                    .overdueAmount(dcAmount)
                    .build();

            cardMapper.insCreditOverdue(co);
        }
        sqlSession.flushStatements();
    }

    @Test
    void Generate3() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        Faker faker = new Faker();

        List<Long> cardIdList = cardMapper.selCardId2();
        List<Long> creditCardIdList = cardMapper.selCreditCardId();
        List<Long> custIdList = cardMapper.selCustId();

        int count = 0;

        for(Long creditCardId : creditCardIdList) {
            int firstNumber = faker.number().numberBetween(1, 10);
            String remainingNumber = faker.numerify("########");
            String fullNumber = firstNumber + remainingNumber;
            int lastNumber = faker.number().numberBetween(0, 10);


            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String rawPassword = faker.numerify("####");
            String encryptedPassword = encoder.encode(rawPassword);

            Long custId = custIdList.get(count);

            count++;
            String uCardCode = "";
                if(count <= creditCardIdList.size() * 0.6) {
                    uCardCode = "00801";
                } else if(count <= creditCardIdList.size() * 0.7) {
                    uCardCode = "00802";
                } else if(count <= creditCardIdList.size() * 0.8) {
                    uCardCode = "00803";
                } else if(count <= creditCardIdList.size() * 0.9) {
                    uCardCode = "00804";
                } else if(count <= creditCardIdList.size()) {
                    uCardCode = "00805";
                }

            int year = ThreadLocalRandom.current().nextInt(2000, 2025);
            int month = ThreadLocalRandom.current().nextInt(1, 13);

            // 해당 년/월의 마지막 일 계산
            YearMonth yearMonth = YearMonth.of(year, month);
            int maxDay = yearMonth.lengthOfMonth(); // 윤년 자동 반영되는 감사한 코드

            // 일 범위 지정 (1 ~ maxDay)
            int day = ThreadLocalRandom.current().nextInt(1, maxDay + 1);

            // LocalDateTime 생성 (시간 고정)
            LocalDateTime createdAt = LocalDate.of(year, month, day).atStartOfDay();
            LocalDateTime endAtLocal = createdAt.plusYears(3);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String endAt = endAtLocal.format(formatter);
            LocalDateTime lostAt = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

            if(uCardCode.equals("00802") || uCardCode.equals("00803") || uCardCode.equals("00804")){
                lostAt = createdAt.plusDays(faker.random().nextInt(1000));
            }

            if(uCardCode.equals("00805")){
                lostAt = endAtLocal.plusDays(1);
            }


            UserCard userCard = UserCard.builder()
                    .uCardId(creditCardId)
                    .cardId(cardIdList.get(faker.random().nextInt(cardIdList.size())))
                    .custId(custId)
                    .cardNumber("504504" + fullNumber + lastNumber)
                    .cardPassword(encryptedPassword)
                    .uCardCode(uCardCode)
                    .createdAt(createdAt)
                    .endAt(endAt)
                    .lostAt(lostAt)
                    .build();
            cardMapper.insUserCreditCard(userCard);
        }
        sqlSession.flushStatements();
    }



}
