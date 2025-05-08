package com.example.projectdummy.card;

import com.example.projectdummy.card.model.CheckCard;
import com.example.projectdummy.card.model.UserCard;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CardMapper {

    int saveCheckCard(CheckCard checkCard);
    int saveDepositCard(UserCard userCard);
    List<Long> findCheckCard();
}
