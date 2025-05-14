package com.example.projectdummy.account;

import com.example.projectdummy.account.model.BankAccount;
import com.example.projectdummy.account.model.LoanUser;
import com.example.projectdummy.account.model.TransactionHistory;
import com.example.projectdummy.customer.model.UseAuthPk;
import com.example.projectdummy.loan.model.LoanAccount;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AccountMapper {

    int saveBankAccount(BankAccount bankAccount);
    int insBankAccount(BankAccount bankAccount);
    int insBankInternalAccount();
    int insLoanAccount(LoanAccount loanAccount);
    List<UseAuthPk> selAccountId();
    List<Long> selTranFeeId();
    LoanUser selLoanUser(Long accountId);
    int insTranHistory(TransactionHistory transactionHistory);
    Long selFinalPk();
    List<UseAuthPk> selAccountByDepositCode(String code);

}
