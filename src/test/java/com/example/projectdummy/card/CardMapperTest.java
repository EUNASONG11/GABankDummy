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
    final Long cnt = 5000L;

    @Test
    void Generate(){
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        Faker faker = new Faker();

        for(Long i=1L; i <= cnt; i++){
            int langType = faker.random().nextInt(2);
            CreditStatement.builder()
                    .creditId(i)
                    .place(langType==0 ? kofaker.company().name() : enfaker.company().name() )
                    .flag(langType)
                    .state(faker.random().nextInt(3))
                    .exploiter(faker.random().nextInt(2)==0?"본인":"가족")
                    .accumPoint(faker.random().nextInt(300001))
                    .discount( BigDecimal.valueOf(2.0 + faker.random().nextDouble() * 7.9)
                            .setScale(1, RoundingMode.HALF_UP))
                    .fee()
                    .ogAmount()
                    .installment()
                    .interestFree()
                    .uYear()
                    .uMonth()
                    .uDay()
                    .build();

        }

    }
}
