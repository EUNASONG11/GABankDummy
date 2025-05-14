package com.example.projectdummy.productAndDeposit;

import com.example.projectdummy.AccountDummyDefault;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CheckBillDummy extends AccountDummyDefault {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    DepositMapper depositMapper;

    final Long CNT = 10000L;

//    @Test
//    void createCheckBill() {
//        List<DepositAccount> allAccounts = depositMapper
//
//
//    }

}
