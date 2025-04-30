package com.example.projectdummy.customer;

import com.example.projectdummy.customer.model.BusinessCorporation;
import com.example.projectdummy.customer.model.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper {
    int insCustomer(Customer customer);
    int updCustomer(Customer customer);
    int insBusinessCorporation(BusinessCorporation businessCorporation);
}
