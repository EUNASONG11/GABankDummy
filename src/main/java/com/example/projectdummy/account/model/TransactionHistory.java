package com.example.projectdummy.account.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class TransactionHistory {
    private Long historyId;
    private Long transactionFeeId;
    private Long accountId;
    private int flag;
    private Long money;
    private String toName;
    private String accountNum;
    private String bankCode;
    private String location;
    private Long hsMoney;
    private String atmCOde;
    private String useAccount;
}
