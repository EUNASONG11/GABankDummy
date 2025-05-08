package com.example.projectdummy.loan;

import com.example.projectdummy.loan.model.Loan;
import com.example.projectdummy.loan.model.LoanRateLog;
import com.example.projectdummy.loan.model.RateLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoanMapper {

    List<Long> selLoan(int useFlag);
    List<Loan> selLoan2(int useFlag);
    int insLoanLog(RateLog rateLog);

    List<Long> selLoanAccountId();
    double selDiscountedRate(long accountId);
    double selAdditionalRate(long accountId);
    int insLoanRateLog(LoanRateLog loanRateLog);
}
