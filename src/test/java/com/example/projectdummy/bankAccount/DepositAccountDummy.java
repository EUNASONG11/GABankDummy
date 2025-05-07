package com.example.projectdummy.bankAccount;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.account.AccountMapper;
import com.example.projectdummy.account.model.BankAccount;
import com.example.projectdummy.employee.EmployeeMapper;
import com.example.projectdummy.productAndDeposit.DepositMapper;
import com.example.projectdummy.productAndDeposit.Product;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Random;

//아직 테스트 안햇음
class DepositAccountDummy extends DummyDefault {

    @Autowired
    AccountMapper accountMapper;
    EmployeeMapper employeeMapper;
    DepositMapper depositMapper;
    final Long CNT = 10000L;

    @Test //요구불
    void createDemandAccount() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        List<Long> depositIds = depositMapper.selDemandDepositId();
        List<Long> selEmployee = employeeMapper.selEmployee();

        Random random = new Random();
        //final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        for(int i = 0 ; i < CNT ; i++) {
            long demandDepositId = depositIds.get(random.nextInt(depositIds.size()));
            boolean success = false;
            int retryCnt = 0;

            while(!success && retryCnt < 10) {
                BankAccount account = new BankAccount();
                account.setAccountNum("50401"+kofaker.numerify("########"));
                account.setEmployeeId(selEmployee.get(kofaker.random().nextInt(selEmployee.size())));
                account.setCustId(kofaker.random().nextLong(10001)+1);
                account.setProductId(demandDepositId);
                account.setAccountPassword(kofaker.numerify("####"));
                account.setMoney(random.nextInt(1_000_000_000));
                account.setStatusCode("00201");

                try {
                    accountMapper.saveBankAccount(account);
                    sqlSession.flushStatements();
                    success = true;
                } catch (Exception e) {
                    retryCnt++;
                    if (isDuplicateKeyException(e)) {
                        System.out.println("Duplicate accountNum detected. Retrying... (" + retryCnt + ")");
                    } else {
                        e.printStackTrace(); // 다른 예외는 출력
                        break; // 중복 외 에러 발생 시 반복 중단
                    }
                }
            }
        }
    }

    @Test //저축
    void createSavingAccount() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);

        List<Long> savingDepositId = depositMapper.selSavingDepositId();
        List<Long> selEmployee = employeeMapper.selEmployee();
        Random random = new Random();



    }




    boolean isDuplicateKeyException(Exception e) {
        return e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate");
    }
}
