package com.example.projectdummy.account;

import com.example.projectdummy.account.model.BankAccount;
import com.example.projectdummy.account.model.BankAccountAnother;
import com.example.projectdummy.customer.model.UseAuthPk;
import com.example.projectdummy.productAndDeposit.Product;
import com.example.projectdummy.loan.model.LoanAccount;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AccountMapper {

    int saveBankAccount(BankAccountAnother bankAccount);
    int insBankAccount(BankAccount bankAccount);
    int insBankInternalAccount();
    int insLoanAccount(LoanAccount loanAccount);
    List<UseAuthPk> selAccountId();
    List<Long> selTranFeeId();

}
