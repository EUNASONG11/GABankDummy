package com.example.projectdummy.customer;

import com.example.projectdummy.card.model.CardOverDue;
import com.example.projectdummy.loan.model.OverdueRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OverdueMapper {
    List<CardOverDue> selCardOverdue(String dueCode);
    int insCardOverdue(OverdueRecord overdueRecord);
}
