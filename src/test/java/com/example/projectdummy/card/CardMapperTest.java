package com.example.projectdummy.card;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.card.model.CreditStatement;
import net.datafaker.Faker;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

            int month = (faker.random().nextInt(11) + 1);
            String randomDay = "";
            if (month == 2) {
                randomDay = String.valueOf(faker.random().nextInt(28) + 1); // 1일부터 28일까지
            } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) { // 31일이 있는 달
                randomDay = String.valueOf(faker.random().nextInt(31) + 1); // 1일부터 31일까지
            } else { // 30일이 있는 달
                randomDay = String.valueOf(faker.random().nextInt(30) + 1); // 1일부터 30일까지
            }

            CreditStatement cs = CreditStatement.builder()
                    .creditId(i)
                    .creditCardId(faker.random().nextLong(100) + 1)
                    .place(companyName)
                    .flag(1)
                    .state(faker.random().nextInt(3))
                    .exploiter(faker.random().nextInt(2)==0?"본인":"가족")
                    .accumPoint(faker.random().nextInt(300001))
                    .discount( BigDecimal.valueOf(2.0 + faker.random().nextDouble() * 7.9)
                            .setScale(1, RoundingMode.HALF_UP))
                    .fee(0)
                    .ogAmount(faker.random().nextInt(9999900) + 100)
                    .installment(0)
                    .interestFree(faker.random().nextInt(5) + 2)
                    .uYear(String.valueOf(faker.random().nextInt(25) + 2000))
                    .uMonth(String.valueOf(month))
                    .uDay(randomDay)
                    .build();
            cardMapper.insCreditStatement(cs);
        }
        sqlSession.flushStatements();

        //25~50% 할부 이자
        for(Long i = (cnt/4) + 1; i < cnt/2; i++) {
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

            int month = (faker.random().nextInt(11) + 1);
            String randomDay = "";
            if (month == 2) {
                randomDay = String.valueOf(faker.random().nextInt(28) + 1); // 1일부터 28일까지
            } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) { // 31일이 있는 달
                randomDay = String.valueOf(faker.random().nextInt(31) + 1); // 1일부터 31일까지
            } else { // 30일이 있는 달
                randomDay = String.valueOf(faker.random().nextInt(30) + 1); // 1일부터 30일까지
            }

            int installment = faker.random().nextInt(35) + 2;
            int ogAmount = faker.random().nextInt(9999900) + 100;
            int fee = 0;
            if(installment == 2){
                fee = (int) Math.round(14.5 * ogAmount);
            }else if(installment == 3){
                fee = (int) Math.round(18.2 * ogAmount);
            }else if(4 <= installment && installment <= 18){
                fee = (int) Math.round(19.7 * ogAmount);
            }else if(19 <= installment ){
                fee = (int) Math.round(19.8 * ogAmount);
            }

            CreditStatement cs = CreditStatement.builder()
                    .creditId(i)
                    .creditCardId(faker.random().nextLong(100) + 1)
                    .place(companyName)
                    .flag(1)
                    .state(faker.random().nextInt(3))
                    .exploiter(faker.random().nextInt(2)==0?"본인":"가족")
                    .accumPoint(faker.random().nextInt(300001))
                    .discount(BigDecimal.valueOf(2.0 + faker.random().nextDouble() * 7.9)
                            .setScale(1, RoundingMode.HALF_UP))
                    .fee(fee)
                    .ogAmount(ogAmount)
                    .installment(installment)
                    .interestFree(0)
                    .uYear(String.valueOf(faker.random().nextInt(25) + 2000))
                    .uMonth(String.valueOf(month))
                    .uDay(randomDay)
                    .build();
            cardMapper.insCreditStatement(cs);
            sqlSession.flushStatements();
        }

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

            int month = (faker.random().nextInt(11) + 1);
            String randomDay = "";
            if (month == 2) {
                randomDay = String.valueOf(faker.random().nextInt(28) + 1); // 1일부터 28일까지
            } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) { // 31일이 있는 달
                randomDay = String.valueOf(faker.random().nextInt(31) + 1); // 1일부터 31일까지
            } else { // 30일이 있는 달
                randomDay = String.valueOf(faker.random().nextInt(30) + 1); // 1일부터 30일까지
            }

            int ogAmount = faker.random().nextInt(9999900) + 100;

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
                    .uYear(String.valueOf(faker.random().nextInt(25) + 2000))
                    .uMonth(String.valueOf(month))
                    .uDay(randomDay)
                    .build();
            cardMapper.insCreditStatement(cs);
        }
        sqlSession.flushStatements();

    }
}
