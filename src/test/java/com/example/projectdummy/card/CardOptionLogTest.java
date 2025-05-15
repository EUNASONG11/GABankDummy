package com.example.projectdummy.card;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.card.model.CardOptionLog;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

        for(int i=0;i<selCardId.size();i++){
            List<String> rateList = new ArrayList<>(Arrays.asList(rateNames));
            Collections.shuffle(rateList);
            int ran = kofaker.random().nextInt(3)+1;
            for(int j=0;j<ran;j++){
                CardOptionLog c = new CardOptionLog();
                c.setUCardId(selCardId.get(j));
                c.setFlag(kofaker.random().nextInt(2));
                c.setCOptionCode(rateList.get(j));
                cardMapper.insCardOptionLog(c);
            }
            int ran2 = kofaker.random().nextInt(100)+1;

            CardOptionLog col = new CardOptionLog();
            col.setUCardId(selCardId.get(i));
            col.setFlag(0);
            col.setCOptionCode("01002");
            if(ran2==50){
                col.setFlag(1);
            }
            cardMapper.insCardOptionLog(col);
            if(i%200==0) {
                sqlSession.flushStatements();
            }
        }
        sqlSession.flushStatements();
        sqlSession.commit();
        sqlSession.close();
    }

}