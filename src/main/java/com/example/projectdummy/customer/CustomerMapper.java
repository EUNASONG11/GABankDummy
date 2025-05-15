package com.example.projectdummy.customer;

import com.example.projectdummy.customer.model.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CustomerMapper {
    int insCustomer(Customer customer);
    int updCustomer(Customer customer);
    int insBusinessCorporation(BusinessCorporation businessCorporation);
    int insOnlineBank(OnlineBank onlineBank);
    int insAuth(CardAccountAuth cardAccountAuth);

    List<CustIdWithCode> findCustCode();
}
