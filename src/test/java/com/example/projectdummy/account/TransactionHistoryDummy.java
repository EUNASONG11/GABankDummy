package com.example.projectdummy.account;

import com.example.projectdummy.DummyDefault;
import com.example.projectdummy.account.model.TransactionHistory;
import com.example.projectdummy.customer.model.UseAuthPk;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

class TransactionHistoryDummy extends DummyDefault {
    @Autowired
    AccountMapper accountMapper;

    @Test
    void historyDummy() {
        int cnt = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        Random random = new Random();
        List<UseAuthPk> selAccountId = accountMapper.selAccountId();
        LocalDateTime endDate = LocalDateTime.now();
        List<Long> selTranFeeId = accountMapper.selTranFeeId();
        List<UseAuthPk> selAccountIdByProduct = accountMapper.selAccountByDepositCode("00505");
        String[] bankDummy = {"356", "1002", "012", "00", "3333", "1000", "110", "000"};
        for (UseAuthPk uap : selAccountIdByProduct) {
            LocalDateTime startDate = uap.getCreatedAt();
            Long money = 0L;
            int setAccount = kofaker.random().nextInt(3) + 12; // 계좌번호용

            // startDate, endDate sd~ed 사이 랜덤
            // for문 created_at으로
            long totalDay = ChronoUnit.DAYS.between(startDate, endDate);
            for (LocalDateTime sd = startDate; !sd.isAfter(endDate.minusDays(3)); sd = sd.plusDays(1)) {
                if (cnt++ % 100000 == 0) {
                    System.out.println(cnt + "번 들어감");
                }



                    sd = sd.plusMinutes(kofaker.random().nextInt(60))
                            .plusSeconds(kofaker.random().nextInt(60))
                            .plusHours(kofaker.random().nextInt(24))
                            .plusDays(kofaker.random().nextInt(3));
                    Long a = kofaker.random().nextLong(4) == 0 ?
                            (kofaker.random().nextLong(10000) + 1) * 100 :
                            (kofaker.random().nextLong(90) + 10) * 100; // 금액 랜덤용

                    String bank = bankDummy[random.nextInt(bankDummy.length)];
                    String newBankAccount;
                    String bankCode;
                    if (bank.equals("00")) { // 국민
                        newBankAccount = kofaker.numerify("##############");
                        bankCode = "004";
                    } else if (bank.equals("012")) {
                        newBankAccount = kofaker.numerify("012########");
                        bankCode = "003";
                    } else if (bank.equals("356")) {
                        newBankAccount = kofaker.numerify("356##########");
                        bankCode = "011";
                    } else if (bank.equals("1002")) {
                        newBankAccount = kofaker.numerify("1002#########");
                        bankCode = "020";
                    } else if (bank.equals("3333")) {
                        newBankAccount = kofaker.numerify("3333########");
                        bankCode = "090";
                    } else if (bank.equals("1000")) {
                        newBankAccount = kofaker.numerify("1000########");
                        bankCode = "090";
                    } else if (bank.equals("000")) { // 출금
                        bankCode = "300";
                        newBankAccount = kofaker.numerify("50401########");
                        // 요구불예금 계좌중 랜덤으로 들고올지 생각
                    } else {
                        newBankAccount = kofaker.numerify("110##########");
                        bankCode = "088";
                    }
                    String atmCode = kofaker.random().nextInt(6) == 0 ? "00301" : "00302"; // 6:1 폰/atm 비율
                    if (atmCode.equals("00301")) {
                        atmCode = kofaker.random().nextInt(8) == 0 ? "00303" : "00301"; //
                    }
                    TransactionHistory th = new TransactionHistory();
                    Long tranFee = selTranFeeId.get(kofaker.random().nextInt(selTranFeeId.size()));
                    int checkFlag = kofaker.random().nextInt(2); // 입출금 확인용
                    th.setTransactionFeeId(tranFee);
                    th.setAccountId(uap.getAccountId());
                    th.setFlag(checkFlag);
                    th.setMoney(a);
                    if (checkFlag == 0) {
                        money -= a;
                        if (money < 0) {
                            money += a * 2;
                            th.setFlag(1);
                        }
                    } else {
                        money += a;
                    }
                    th.setToName(kofaker.name().lastName() + kofaker.name().firstName());
                    th.setAccountNum(newBankAccount);
                    th.setToBankCode(bankCode); // bankCode 공통코드에 넣고 불러올지 String 배열 직접 작성할지
                    th.setCreatedAt(sd); // 카드/계좌에 사용한 랜덤시간으로 돌아가면서
                    th.setLocation(kofaker.address().fullAddress());
                    th.setHsMoney(money);
                    th.setAtmCode(atmCode);


                    if (checkFlag == 0) {
                        if (atmCode.equals("00301")) { // 출금일때를 위해
                            th.setToBankCode("300");
                            th.setToName("현금출금");
                            th.setAccountNum(kofaker.numerify("50490########"));
                            // 내부계좌에서 랜덤으로 들고올지(용도 code로) 01802
                        }
                    }

                    accountMapper.insTranHistory(th);
                if(cnt%1000 ==0){
                    sqlSession.flushStatements();
                    System.out.println(cnt);
                }
            }
            sqlSession.flushStatements();
        }
        sqlSession.flushStatements();
            sqlSession.commit();
            sqlSession.close();
    }
}


