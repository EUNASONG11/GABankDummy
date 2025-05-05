package com.example.projectdummy.loan;

import com.example.projectdummy.loan.model.RateLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoanMapper {

    List<Long> selLoan();
    int insLoanLog(RateLog rateLog);
}
