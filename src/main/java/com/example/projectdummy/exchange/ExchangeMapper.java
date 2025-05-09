package com.example.projectdummy.exchange;

import com.example.projectdummy.exchange.model.ExchangeLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExchangeMapper {
    List<String> selCurrency();
    int insExchangeLog(ExchangeLog exchangeLog);
}
