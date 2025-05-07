package com.example.projectdummy.account;

import com.example.projectdummy.account.model.BankAccount;
import com.example.projectdummy.productAndDeposit.Product;
import com.example.projectdummy.loan.model.LoanAccount;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AccountMapper {
    List<Long> findCustomer();
    List<Long> findEmployee();

    int saveBankAccount(BankAccount bankAccount);
    int insBankAccount(BankAccount bankAccount);
    int insBankInternalAccount();
    int insLoanAccount(LoanAccount loanAccount);

}
