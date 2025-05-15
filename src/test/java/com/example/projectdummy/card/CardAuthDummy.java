package com.example.projectdummy.card;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.account.AccountMapper;
import com.example.projectdummy.customer.CustomerMapper;
import com.example.projectdummy.customer.model.CardAccountAuth;
import com.example.projectdummy.customer.model.UseAuthPk;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CardAuthDummy extends DummyDefault {
    @Autowired
    CardMapper cardMapper;
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    CustomerMapper customerMapper;
    @Test
    void cardAuth(){
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        // selAccountId &lt; 는 <,  &gt; 는 >
        List<UseAuthPk> selAccountId = accountMapper.selAccountId();

        LocalDateTime endDate = LocalDateTime.now();
        int cnt = 0;

        for(UseAuthPk uap:selAccountId){
            LocalDateTime startDate = uap.getCreatedAt();
            CardAccountAuth ca = new CardAccountAuth();
            ca.setDivisionId(uap.getAccountId());
            ca.setFlag((uap.getProductId() > 200_000) ? 1 : 0);
            ca.setState(0);
            cnt++;
            while(startDate.isBefore(endDate)){
                ca.setLocation(kofaker.address().fullAddress()); // 위치 어케 넣을지 생각 일단  >> 랜덤으로 넣음
                ca.setCreatedAt(startDate); // createdAt 넣기위한 for문(2022년도~오늘)
                int addDay = kofaker.random().nextInt(30)+3;
                int addMinute = kofaker.random().nextInt(50)+1;
                startDate = startDate.plusDays(addDay);
                startDate = startDate.plusMinutes(addMinute);
                cnt++;
                customerMapper.insAuth(ca);
                if(cnt%1000==0){
                    sqlSession.flushStatements();
                }
            }
            int randomInt = kofaker.random().nextInt(300)+1;
            int randomInt2 = kofaker.random().nextInt(6)+1;
            ca.setLocation(kofaker.address().fullAddress());
            if(!startDate.isBefore(endDate) && randomInt == 100){
                ca.setState(1);
                ca.setCreatedAt(endDate);
                customerMapper.insAuth(ca);
                // 최신일 기준으로 드물게 인증실패가 있도록(1천건당 1번?) > 일단 계좌가 많이 없어서 300건당 1번으로
            }
            if(randomInt == 100 && randomInt2 == 3){
                ca.setState(1);
                ca.setCreatedAt(endDate.plusMinutes(2));
                customerMapper.insAuth(ca);
                // 혹시 2연속 틀릴 경우
            }

//            if(!startDate.isBefore(endDate) && kofaker.random().nextInt(300)+1 == 100){
//                ca.setLocation(kofaker.address().fullAddress());
//                ca.setState(1);
//                ca.setCreatedAt(endDate);
//                customerMapper.insAuth(ca);
//                // 최신일 기준으로 드물게 인증실패가 있도록(1천건당 1번?) > 일단 계좌가 많이 없어서 300건당 1번으로
//            }
            sqlSession.flushStatements();
        }
        sqlSession.flushStatements();
        sqlSession.commit();
        sqlSession.close();
    }
}
