package com.example.projectdummy.account;

import com.example.projectdummy.account.model.BankAccount;
import com.example.projectdummy.customer.model.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper {
    int insBankAccount(BankAccount bankAccount);
    int updBankAccount(BankAccount bankAccount);
}
