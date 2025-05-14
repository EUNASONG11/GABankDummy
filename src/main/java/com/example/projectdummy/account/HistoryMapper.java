package com.example.projectdummy.account;

import com.example.projectdummy.account.model.TransactionHistory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HistoryMapper {
    int saveHistories(List<TransactionHistory> histories);
}
