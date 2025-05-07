package com.example.projectdummy.account;

import com.example.projectdummy.account.model.BankAccount;
import com.example.projectdummy.productAndDeposit.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccountMapper {
    List<Product> findProduct();
    List<Long> findCustomer();
    List<Long> findEmployee();

    int saveBankAccount(BankAccount bankAccount);

}
