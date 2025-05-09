package com.example.projectdummy;

import com.example.projectdummy.account.DocumentMapper;
import com.example.projectdummy.account.model.ContractDocument;
import com.example.projectdummy.account.model.ProductDocumentWithName;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

// 계좌 생성시 서류 생성하는 메서드를 담은 클래스
public class AccountDummyDefault extends DummyDefault{
    @Autowired
    DocumentMapper documentMapper;


    public void savingContractDocument(long productId, Long contractId, int flag) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        List<ProductDocumentWithName> productDocuments = documentMapper.findProductDocument(productId);

        for(ProductDocumentWithName pd : productDocuments) {
            ContractDocument contractDocument = ContractDocument.builder()
                    .contractId(contractId)
                    .document(pd.getDocumentName()) //나중에 파일명이나 경로로 수정해둘 것.
                    .productDocumentId(pd.getProductDocumentId())
                    .flag(flag)
                    .build();

            documentMapper.saveContractDocument(contractDocument);
        }
        sqlSession.flushStatements();
    }
}
