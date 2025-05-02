package com.example.projectdummy.bankAccount;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.account.AccountMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BankAccountDummy  extends DummyDefault {

    @Autowired
    private AccountMapper accountMapper;


    @Test
    void test() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);


    }
}
