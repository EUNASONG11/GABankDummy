package com.example.projectdummy.productAndDeposit;

import com.example.projectdummy.account.model.ContractDocument;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {
    List<Long> selProductDocument(Long productId);
    int insContractDocument(ContractDocument contractDocument);
    int insCheckBill(CheckBill checkBill);
    List<DepositAccount> findDepositAccount();
}
