package com.example.projectdummy.card;

import com.example.projectdummy.DummyDefault;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardOptionLogTest extends DummyDefault {

    @Autowired
    CardMapper cardMapper;

    @Test
    void optionLogTest() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        String[] rateNames = {
                "01001", "01003",  "01004", "01005"
        };
        // "01002"
        List<Long> selCardId = cardMapper.selCardId();
    }

}