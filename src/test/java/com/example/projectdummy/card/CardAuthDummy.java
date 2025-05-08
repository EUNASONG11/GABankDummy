package com.example.projectdummy.card;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.account.AccountMapper;
import com.example.projectdummy.account.model.BankAccount;
import com.example.projectdummy.customer.model.CardAccountAuth;
import com.example.projectdummy.customer.model.UseAuthPk;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class CardAuthDummy extends DummyDefault {
    @Autowired
    CardMapper cardMapper;
    @Autowired
    AccountMapper accountMapper;
    @Test
    void cardAuth(){
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        // selAccountId &lt; 는 <,  &gt; 는 >
        List<UseAuthPk> selAccountId = accountMapper.selAccountId();

        for(UseAuthPk uap:selAccountId){
            CardAccountAuth ca = new CardAccountAuth();
            ca.setDivision_id(uap.getAccountId());
            ca.setFlag((uap.getProductId() > 200_000) ? 1 : 0);
            ca.setState(0);
            // ca.setLocation(); 위치 어케 넣을지 생각
            // createdAt 넣기위한 for문(2022년도~오늘)
            // ca.setCreatedAt();
            // 최신일 기준으로 드물게 인증실패가 있도록(1천건당 1번?)
        }

    }
}
