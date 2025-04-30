package com.example.projectdummy.customer;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.customer.model.Customer;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CustomerMapperTest extends DummyDefault {
    @Autowired
    CustomerMapper customerMapper;
    final Long cnt = 10000L;

    @Test
    void Generate(){
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        CustomerMapper customerMapper1 = sqlSession.getMapper(CustomerMapper.class);

        for(Long i=1L;i<=cnt;i++){

            Customer customer = Customer.builder()
                    .custId(i)
                    .custName(kofaker.name().lastName() + kofaker.name().firstName())
                    .email(i+enfaker.internet().emailAddress())
                    .phone(kofaker.phoneNumber().cellPhone())
                    .birth(kofaker.date().birthday(18,70).toString())
                    .creditPoint(kofaker.random().nextInt(401)+600)
                    .custCode("0010"+ ((i%3)+1))
                    .build();
            customerMapper.insCustomer(customer);
            sqlSession.flushStatements();
        }
        // 300~600 4퍼 300아래 0.5프로
        for(int i=0;i<50;i++){
            Customer customer = Customer.builder()
                    .custId(kofaker.random().nextLong(10000)+1)
                    .creditPoint(kofaker.random().nextInt(301)+1)
                    .build();
            customerMapper.updCustomer(customer);
            sqlSession.flushStatements();
        }
    }
}