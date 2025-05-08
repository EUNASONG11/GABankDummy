package com.example.projectdummy;

import com.example.projectdummy.account.DocumentMapper;
import com.example.projectdummy.account.model.AccountDocument;
import com.example.projectdummy.account.model.Document;
import com.example.projectdummy.account.model.ProductDocumentWithName;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

// 계좌 생성시 서류 생성하는 메서드를 담은 클래스
public class AccountDummyDefault extends DummyDefault{
    @Autowired
    DocumentMapper documentMapper;


    public void savingAccountDocument(long productId, Long accountId) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        List<ProductDocumentWithName> productDocuments = documentMapper.findProductDocument(productId);

        for(ProductDocumentWithName pd : productDocuments) {
            AccountDocument accountDocument = AccountDocument.builder()
                    .accountId(accountId)
                    .document(pd.getDocumentName()) //나중에 파일명이나 경로로 수정해둘 것.
                    .productDocumentId(pd.getProductDocumentId())
                    .build();

            documentMapper.saveAccountDocument(accountDocument);
        }
        sqlSession.flushStatements();
    }
}
