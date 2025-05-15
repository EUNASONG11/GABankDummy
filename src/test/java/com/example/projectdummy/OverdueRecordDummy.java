package com.example.projectdummy;

import com.example.projectdummy.card.model.CardOverDue;
import com.example.projectdummy.customer.OverdueMapper;
import com.example.projectdummy.loan.LoanMapper;
import com.example.projectdummy.loan.model.LoanOverdue;
import com.example.projectdummy.loan.model.OverdueRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class OverdueRecordDummy extends  DummyDefault{
    @Autowired
    OverdueMapper overdueMapper;
    @Autowired
    private LoanMapper loanMapper;

    @Test
    void dummy(){
        // 1. 대출
        int cnt = loanMapper.selOverdueCnt();
        for (int i = 0; i < cnt; i++) {
            LoanOverdue loanOverdue = loanMapper.selLoanOverdue(cnt + 1);
            OverdueRecord overdueRecord = new OverdueRecord();
            overdueRecord.setRemain((long)0);
            overdueRecord.setOverdueMoney(loanOverdue.getOverdueMoney() + loanOverdue.getOverdueInterest());
            overdueRecord.setTargetId(loanOverdue.getOverdueId());

        }
    }
    @Test
    void cardOverdueRecord(){

        // 1. 신용카드 연체에 존재하는 pk 들고옴 이떄 due_code가 01204인것 부터
        List<CardOverDue> cod = overdueMapper.selCardOverdue("01204");

        for(CardOverDue cardOverDue : cod){
            OverdueRecord odr =  new OverdueRecord();
            odr.setTargetId(cardOverDue.getOverdueId());
            odr.setOverdueMoney(cardOverDue.getOverdueAmount());
            odr.setRemain(0L);
            odr.setOverdueCode("01301");
            odr.setStartAt(cardOverDue.getDueAt());
            odr.setCreatedAt(cardOverDue.getPaidAt());

            if(kofaker.random().nextInt(50)==1){
                Long rm = kofaker.random().nextLong(cardOverDue.getOverdueAmount()-1000)+1000;
                // 랜덤 상환금을 위해 남은 상환금
                odr.setRemain(rm);
                odr.setOverdueMoney(cardOverDue.getOverdueAmount()-rm);
                odr.setCreatedAt(cardOverDue.getPaidAt().plusDays(kofaker.random().nextInt(24)+7));

                OverdueRecord odr1 =  new OverdueRecord();
                odr1.setTargetId(cardOverDue.getOverdueId());
                odr1.setOverdueCode("01301");
                odr1.setStartAt(cardOverDue.getDueAt());
                odr1.setCreatedAt(cardOverDue.getPaidAt());
                odr1.setRemain(0L);
                odr1.setOverdueMoney(rm);
                overdueMapper.insCardOverdue(odr1);
            }
            overdueMapper.insCardOverdue(odr);
        }
        List<CardOverDue> cod1 = overdueMapper.selCardOverdue("01203");
        // 01203이지만 일부만 납부
        int a = 0;
        for(CardOverDue cardOverDue : cod1){
            a+=1;
            if(a%50==0) {
                OverdueRecord odr = new OverdueRecord();
                odr.setTargetId(cardOverDue.getOverdueId());
                Long rm = kofaker.random().nextLong(cardOverDue.getOverdueAmount() - 1000) + 1000;
                // 랜덤 상환금을 위해 남은 상환금
                odr.setRemain(rm);
                odr.setOverdueMoney(cardOverDue.getOverdueAmount() - rm);
                odr.setCreatedAt(cardOverDue.getPaidAt().plusDays(kofaker.random().nextInt(5) + 3));
                odr.setOverdueCode("01301");
                odr.setStartAt(cardOverDue.getDueAt());
                overdueMapper.insCardOverdue(odr);
            }
        }
    }
}
