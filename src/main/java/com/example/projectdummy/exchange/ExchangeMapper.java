package com.example.projectdummy.exchange;

import com.example.projectdummy.exchange.model.ExchangeLog;
import com.example.projectdummy.exchange.model.ForeignExchange;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExchangeMapper {
    List<String> selCurrency();
    int insExchangeLog(ExchangeLog exchangeLog);

    List<ExchangeLog> selExchangeLogWithCreatedAt();
    List<Long> selBankAccount();
    List<Long> selEmployee();

    int insForeignExchange(ForeignExchange foreignExchange);
}
