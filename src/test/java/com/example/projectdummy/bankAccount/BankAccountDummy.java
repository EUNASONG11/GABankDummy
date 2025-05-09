package com.example.projectdummy.bankAccount;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.account.AccountMapper;
import com.example.projectdummy.account.model.BankAccount;
import com.example.projectdummy.productAndDeposit.Product;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Random;

//아직 테스트 안햇음
class BankAccountDummy  extends DummyDefault {

    @Autowired
    AccountMapper accountMapper;
    final Long CNT = 10000L;

//    @Test
//    void createBankAccount() {
//        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
//
//        List<Product> products = accountMapper.findProduct();
//        List<Long> employeeIds = accountMapper.findEmployee();
//        List<Long> customerIds = accountMapper.findCustomer();
//
//        Random random = new Random();
//        final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//        for(int i = 0 ; i < CNT ; i++) {
//            Product product = products.get(random.nextInt(products.size()));
//            boolean success = false;
//            int retryCnt = 0;
//
//            while(!success && retryCnt < 10) {
//                String accountNum = makeRandomAccount(product.getProductCode());
//
//                BankAccount account = BankAccount.builder()
//                        .employeeId(employeeIds.get(random.nextInt(employeeIds.size())))
//                        .custId(customerIds.get(random.nextInt(customerIds.size())))
//                        .productId(product.getProductId())
//                        .accountNum(accountNum)
//                        .accountPassword(encoder.encode(String.valueOf(random.nextInt(9000) + 1000)))
//                        .money(random.nextInt(1_000_000_000))
//                        .statusCode("0")
//                        .build();
//
//                try {
//                    accountMapper.saveBankAccount(account);
//                    sqlSession.flushStatements();
//                    success = true;
//                } catch (Exception e) {
//                    retryCnt++;
//                    if (isDuplicateKeyException(e)) {
//                        System.out.println("Duplicate accountNum detected. Retrying... (" + retryCnt + ")");
//                    } else {
//                        e.printStackTrace(); // 다른 예외는 출력
//                        break; // 중복 외 에러 발생 시 반복 중단
//                    }
//                }
//            }
//        }
//
//    }

    boolean isDuplicateKeyException(Exception e) {
        return e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate");
    }

    // 랜덤 계좌 생성기
    String makeRandomAccount(String code) {
        String disit = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(504);

        switch (code){
            case "00401": sb.append("01"); break;
            case "00402": sb.append("02"); break;
            case "00403": sb.append("03"); break;
        }

        for(int i=0; i< 8; i++){
            sb.append(disit.charAt(random.nextInt(disit.length())));
        }
        return sb.toString();
    }
}
