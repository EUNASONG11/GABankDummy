package com.example.projectdummy.account;

import com.example.projectdummy.account.model.ContractDocument;
import com.example.projectdummy.account.model.Document;
import com.example.projectdummy.account.model.ProductDocumentWithName;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DocumentMapper {
    List<Document> findDocument(String code);

    List<ProductDocumentWithName> findProductDocument(long productId);

    int saveContractDocument(ContractDocument contractDocument);
}
