package com.example.projectdummy.customer;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.customer.model.BusinessCorporation;
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

        for(Long i=1L;i<=cnt;i++){

            Customer customer = new Customer();
            customer.setCustId(i);
            customer.setCustName(kofaker.name().lastName() + kofaker.name().firstName());
            customer.setEmail(i+enfaker.internet().emailAddress());
//            customer.setPhone(kofaker.phoneNumber().cellPhone());
            customer.setPhone(kofaker.numerify("010-####-####"));
            customer.setBirth(kofaker.date().birthday(18,70).toString());
            customer.setCreditPoint(kofaker.random().nextInt(401)+600);
            customer.setCustCode("00101");

            customerMapper.insCustomer(customer);
            sqlSession.flushStatements();
        }
        // 300~600 4퍼 300아래 0.5프로
        for(int i=0;i<cnt/200;i++){
            Customer customer = new Customer();
            customer.setCustId(kofaker.random().nextLong(10001)+1);
            customer.setCreditPoint(kofaker.random().nextInt(301)+1);

            customerMapper.updCustomer(customer);
            sqlSession.flushStatements();
        }
    }
    @Test
    void updateCustomer(){

        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
//        // 사업자8 법인2 일반90
//        for(int i=0;i<cnt*2/25;i++){
//            Customer customer = new Customer();
//            customer.setCustId(kofaker.random().nextLong(10001)+1);
//            customer.setCustCode("00102");
//
//            customerMapper.updCustomer(customer);
//            sqlSession.flushStatements();
//        }
//
//        for(int i=0;i<cnt/50;i++){
//            Customer customer = Customer.builder()
//                    .custId(kofaker.random().nextLong(10001)+1)
//                    .custCode("00103")
//                    .build();
//            customerMapper.updCustomer(customer);
//            sqlSession.flushStatements();
//        }

        for(int i=0;i<cnt/10;i++){
            Customer customer = new Customer();
            Long custId = kofaker.random().nextLong(10001)+1;
            customer.setCustId(custId);
            customer.setCustCode("00102");
            BusinessCorporation buco = new BusinessCorporation();
            buco.setCustId(custId);
            buco.setBusinessNumber(kofaker.numerify("###-##-#####"));
            buco.setFax(kofaker.phoneNumber().phoneNumber());
            buco.setCompanyName(kofaker.company().name());
            if(i%5==0){
                customer.setCustCode("00103");
                buco.setCorporationNumber(kofaker.numerify("####-#######"));
            }
            try {
                customerMapper.updCustomer(customer);
                customerMapper.insBusinessCorporation(buco);
                sqlSession.flushStatements();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}