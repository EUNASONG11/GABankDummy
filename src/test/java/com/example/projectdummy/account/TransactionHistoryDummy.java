package com.example.projectdummy.account;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.account.model.TransactionHistory;
import com.example.projectdummy.customer.model.UseAuthPk;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

class TransactionHistoryDummy extends DummyDefault {
    @Autowired
    AccountMapper accountMapper;

    @Test
    void historyDummy(){
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        List<UseAuthPk> selAccountId = accountMapper.selAccountId();
        LocalDateTime endDate = LocalDateTime.now();
        List<Long> selTranFeeId = accountMapper.selTranFeeId();
        String[] bankCode = {"a"};
        for(UseAuthPk uap:selAccountId){
            LocalDateTime startDate = uap.getCreatedAt();
            Long money = 0L;
            Long a = (kofaker.random().nextLong(30000001)+1)*10; // 금액 랜덤용
            int checkFlag = kofaker.random().nextInt(2); // 입출금 확인용
            int setAccount = kofaker.random().nextInt(3)+12; // 계좌번호용
            TransactionHistory th = new TransactionHistory();
            th.setTransactionFeeId(selTranFeeId.get(kofaker.random().nextInt(selTranFeeId.size())));
            th.setAccountId(uap.getAccountId());
            th.setFlag(0);
            th.setMoney(a);
            money -= a;
            th.setAtmCOde("00302");

            // 여기서 부터 created_at에 따라서 들어가도록 for문 안에
            th.setToName(kofaker.name().lastName()+kofaker.name().firstName());
            th.setAccountNum(kofaker.numerify("#".repeat(setAccount)));
            // th.setToBankCode(); // bankCode 공통코드에 넣고 불러올지 String 배열 직접 작성할지
            // th.setCreatedAt(); // 카드/계좌에 사용한 랜덤시간으로 돌아가면서
            th.setLocation(kofaker.address().fullAddress());
            th.setHsMoney(money);
            // th.setUseAccount(); 필요 없는거아님? 계좌id pk가 들어가는데?

        }
    }


}