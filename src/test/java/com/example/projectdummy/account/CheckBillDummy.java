package com.example.projectdummy.account;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.productAndDeposit.CheckBill;
import com.example.projectdummy.productAndDeposit.ProductMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;


public class CheckBillDummy extends DummyDefault {

    @Autowired
    AccountMapper accountMapper;

    final Long CNT = 10_000L;
    Random random = new Random();

    @Test
    void createCheckBill() {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        ProductMapper productMapper = sqlSession.getMapper(ProductMapper.class);

        List<Long> accountIds = accountMapper.selAvailableCheckBillAccounts();
        if (accountIds.isEmpty()) {
            System.out.println("유효한 당좌예금 계좌가 없습니다.");
        }

        for (int i = 0; i < CNT; i++) {
            Long accountId = accountIds.get(random.nextInt(accountIds.size()));

            boolean isNote = random.nextBoolean();
            int typeFlag = isNote ? 1 : 0;

            // 10자리 checkBill 번호 (지점 2자리 + 랜덤 8자리 숫자)
            String branchCode = String.format("%02d", random.nextInt(7) + 1); //01~07
            String checkBill = branchCode + String.format("%08d", random.nextInt(100_000_000));

            // use_flag: 10% 확률로 사용됨(0), 나머지 1
            int useFlag = random.nextInt(10) == 0 ? 0 : 1;

            // 어음일 경우 6~24개월, 수표는 0
            int duration = isNote ? (random.nextInt(4) + 1) * 6 : 0;

            // 생성일 및 사용일
            LocalDate createdAt = LocalDate.now().minusDays(random.nextInt(1000));

            LocalDate usedAt = isNote
                    ? createdAt.plusMonths(duration)
                    : createdAt.plusDays(random.nextInt(60) + 1);

            // 금액
            Long money = isNote
                    ? 1_000_000L + random.nextInt(50_000_000 - 1_000_000 + 1) // 어음: 1,000,000 ~ 50,000,000
                    : 100_000L + random.nextInt(3_000_000 - 100_000 + 1); // // 수표: 100,000 ~ 3,000,000

            // 수취인 이름
            String recipientName = isNote
                    ? kofaker.company().name()
                    : (random.nextInt(10) < 3 ? null : kofaker.name().fullName());

            if (recipientName == null) {
                recipientName = "없음";
            }

            // insert 수행
            CheckBill bill = CheckBill.builder()
                    .checkBill(checkBill)
                    .accountId(accountId)
                    .money(money)
                    .useFlag(useFlag)
                    .typeFlag(typeFlag)
                    .recipientName(recipientName)
                    .duration(duration)
                    .usedAt(usedAt)
                    .createdAt(createdAt).build();

            productMapper.insCheckBill(bill);

            if (i % 1000 == 0) {
                sqlSession.commit();
                System.out.println(i + "건 생성 완료");
            }
        }

        sqlSession.commit();
        sqlSession.close();
        System.out.println("총 " + CNT + "건 수표/어음 생성 완료");
    }
}







