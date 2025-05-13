package com.example.projectdummy.card;

import com.example.projectdummy.card.model.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CardMapper {
    int insCreditStatement(CreditStatement creditStatement);
    int insCreditCardPayment(CreditCardPayment creditCardPayment);
    List<Long> selCreditCardPaymentId();
    Long selDcAmount(Long CreditPaymentId);
    int insCreditOverdue(CreditOverdue creditOverdue);


    int saveCheckCard(CheckCard checkCard);
    int saveDepositCard(UserCard userCard);
    List<Long> findCheckCard();
    List<Long> selCardId();
    int insCardOptionLog(CardOptionLog cardOptionLog);
}
