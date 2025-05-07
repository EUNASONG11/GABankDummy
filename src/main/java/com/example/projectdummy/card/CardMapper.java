package com.example.projectdummy.card;

import com.example.projectdummy.card.model.CreditStatement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CardMapper {
    int insCreditStatement(CreditStatement creditStatement);
    int updCreditStatement(CreditStatement creditStatement);
}
