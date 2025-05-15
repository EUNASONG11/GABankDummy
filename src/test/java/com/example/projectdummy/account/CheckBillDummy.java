package com.example.projectdummy.account;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.account.model.CheckBillInfo;
import com.example.projectdummy.productAndDeposit.CheckBill;
import com.example.projectdummy.productAndDeposit.ProductMapper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

        List<CheckBillInfo> accountIds = accountMapper.selAvailableCheckBillAccounts();
        if (accountIds.isEmpty()) {
            System.out.println("유효한 당좌예금 계좌가 없습니다.");
        }

        for (int i = 0; i < CNT; i++) {
            int ri = random.nextInt(accountIds.size());

            Long accountId = accountIds.get(ri).getAccountId();

            // boolean isNote = random.nextBoolean(); 가중치 줘서 뺄게요
            boolean isNote = kofaker.random().nextInt(5) == 0;
            int typeFlag = isNote ? 1 : 0; // 1/5로 어음

            // 10자리 checkBill 번호 (지점 2자리 + 랜덤 8자리 숫자)
            String branchCode = String.format("%02d", random.nextInt(7) + 1); //01~07
            String checkBill = branchCode + String.format("%08d", random.nextInt(100_000_000));

            // use_flag: 10% 확률로 사용됨(0), 나머지 1 >> create_at과 use_at으로 사용했는지 안했는지 확인해야됨
            int useFlag = random.nextInt(10) == 0 ? 0 : 1;

            // 어음일 경우 6~24개월, 수표는 0 원래는 수표도 기한 주는게 맞는데 그냥 안주는걸로 함
            int duration = isNote ? (random.nextInt(4) + 1) * 6 : 0;

            // 생성일 및 사용일 오늘부터 1천일 이전까지 랜덤인데 계좌들고올 때 created_at까지 들고와서 create_at~오늘까지로 하는게?
            LocalDateTime createdAt1 = accountIds.get(ri).getCreatedAt();
            LocalDateTime endAt = LocalDateTime.now();
            long daysBetween = ChronoUnit.DAYS.between(createdAt1, endAt);
            LocalDate createdAt = createdAt1.toLocalDate().plusDays(random.nextLong(daysBetween));

            // LocalDate createdAt = LocalDate.now().minusDays(random.nextInt(1000));

            LocalDate usedAt = isNote
                    ? createdAt.plusMonths(duration)
                    : createdAt.plusDays(random.nextInt(60) + 1);
            if(usedAt.isAfter(LocalDate.now())) {
                usedAt = LocalDate.parse("9999-12-31");
            }

            if(typeFlag ==1){
                usedAt = createdAt.plusMonths(duration);
            }
            if(usedAt.isBefore(LocalDate.now())) {
                useFlag = 0;

            } else {
                useFlag = 1;
            }

            if(kofaker.random().nextInt(200)==0 && typeFlag == 0){
                useFlag = 1;
                usedAt = LocalDate.parse("9999-12-31");
            } // 수표지만 사용하지 않은 경우들

            // 금액
            Long money = isNote
                    ? 1_000_000L + random.nextInt(50_000_000 - 1_000_000 + 1) // 어음: 1,000,000 ~ 50,000,000
                    : 100_000L + random.nextInt(3_000_000 - 100_000 + 1); // // 수표: 100,000 ~ 3,000,000

            // 수취인 이름
            String recipientName = isNote
                    ? kofaker.company().name()
                    : (random.nextInt(10) < 3 ? "없음" : kofaker.name().fullName());

//            if (recipientName == null) {
//                recipientName = "없음";
//            }

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







