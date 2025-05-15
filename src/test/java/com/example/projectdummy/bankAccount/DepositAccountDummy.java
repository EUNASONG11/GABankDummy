package com.example.projectdummy.bankAccount;

import com.example.projectdummy.AccountDummyDefault;
import com.example.projectdummy.account.AccountMapper;
import com.example.projectdummy.account.model.BankAccount;
import com.example.projectdummy.card.CardMapper;
import com.example.projectdummy.card.model.CheckCard;
import com.example.projectdummy.card.model.UserCard;
import com.example.projectdummy.customer.CustomerMapper;
import com.example.projectdummy.customer.model.CustIdWithCode;
import com.example.projectdummy.employee.EmployeeMapper;
import com.example.projectdummy.productAndDeposit.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

class DepositAccountDummy extends AccountDummyDefault {

    @Autowired
    AccountMapper accountMapper;
    @Autowired
    EmployeeMapper employeeMapper;
    @Autowired
    DepositMapper depositMapper;
    @Autowired
    CardMapper cardMapper;
    @Autowired
    CustomerMapper customerMapper;
    final Long CNT = 10000L;

    @Test //요구불, 상품계약, 체크카드 더미 생성
    void createDemandAccount() {
        List<ProductDeposit> bussiDeposits = depositMapper.selProductDeposit(2);
        List<ProductDeposit> commonDeposits = depositMapper.selProductDeposit(3);
        List<Long> selEmployee = employeeMapper.selEmployee();
        List<Long> checkCards=cardMapper.findCheckCard();
        LocalDateTime localDateTime = LocalDateTime.of(2010, 1, 1, 0, 1);
        List<CustIdWithCode> custs = customerMapper.findCustCode();
        Random random = new Random();
        //final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        //요구불
        for(int i = 0 ; i < CNT ; i++) {
            if(localDateTime.isAfter(LocalDateTime.now())){
                break;
            }
            boolean success = false;
            int retryCnt = 0;
            CustIdWithCode cust=custs.get(random.nextInt(custs.size()));
            ProductDeposit demandDeposit = cust.getCustCode().equals("00101")? commonDeposits.get(random.nextInt(commonDeposits.size()))
                    : bussiDeposits.get(random.nextInt(bussiDeposits.size()));
            BankAccount account = new BankAccount();

            account.setAccountNum("50401"+kofaker.numerify("########"));
            account.setEmployeeId(selEmployee.get(kofaker.random().nextInt(selEmployee.size())));
            account.setCustId(cust.getCustId());
            account.setProductId(demandDeposit.getDepositId());
            account.setAccountPassword(kofaker.numerify("####"));
            account.setMoney(0);
            account.setStatusCode("00201");
            account.setCreatedAt(localDateTime);
            //계좌 중복시 10번까지는 계좌번호 바꾸며 새로 넣도록 함.
            while(!success && retryCnt < 10) {
                try {
                    accountMapper.saveBankAccount(account);
                    success = true;
                    savingContractDocument(demandDeposit.getDepositId(), account.getAccountId(),"00401", localDateTime);
                } catch (Exception e) {
                    retryCnt++;
                    account.setAccountNum("50401"+kofaker.numerify("########"));
                    if (isDuplicateKeyException(e)) {
                        System.out.println("Duplicate accountNum detected. Retrying... (" + retryCnt + ")");
                    } else {
                        e.printStackTrace(); // 다른 예외는 출력
                        break; // 중복 외 에러 발생 시 반복 중단
                    }
                }
            }

            if(cust.getCustCode().equals("00101") && success) {
                //체크카드 insert
                int cardRetryCnt = 0;
                boolean cardSuccess = false;
                CheckCard checkCard = CheckCard.builder()
                        .accountId(account.getAccountId())
                        .build();
                cardMapper.saveCheckCard(checkCard);

                UserCard userCard = UserCard.builder()
                            .uCardId(checkCard.getCheckCardId())
                            .cardId(checkCards.get(new Random().nextInt(checkCards.size())))
                            .custId(account.getCustId())
                            .cardNumber("502502"+kofaker.numerify("##########"))
                            .uCardCode("00801")
                            .cardPassword(kofaker.numerify("####"))
                            .createdAt(localDateTime)
                            .endAt(localDateTime.plusYears(3).toString())
                            .build();
                while (cardRetryCnt < 3&&!cardSuccess) {
                    try {
                        cardMapper.saveDepositCard(userCard);
                        cardSuccess = true;
                        //카드 개설 서류 저장
                        savingContractDocument(userCard.getCardId(),checkCard.getCheckCardId(),"00403", localDateTime);
                    }catch (Exception e) {
                        cardRetryCnt++;
                        userCard.setCardNumber("502502"+kofaker.numerify("##########"));
                        if (isDuplicateKeyException(e)) {
                            System.out.println("Duplicate accountNum detected. Retrying... (" + cardRetryCnt + ")");
                        } else {
                            e.printStackTrace();
                            break;
                        }
                    }
                }

                localDateTime = localDateTime.plusDays(kofaker.random().nextInt(2)).plusMinutes(kofaker.random().nextInt(30))
                        .plusHours(kofaker.random().nextInt(23));
                // 이부분 때문에 더미에 들어가는 양이 줄어듬
                // cnt 는 1만이지만 여기서 시간이 더해지다가 위에 break로 for문이 깨져서 문제생김
                // 따라서 만들거라면 for문 하나더 밖에 만들어서 요구불예금 별로 돌게 하거나 외부 for문의 cnt가 충족될 때 까지 돌려야함

                // 혹은 계좌개설한 사람의 정보를 끌고와서 그사람의 cust_code에 따라 당좌예금 혹은 일반요구불예금이 만들어지도록(정합성) >> 아마 지금은 일반고객도 상품지정이 랜덤이라 당좌예금이 만들어질거
                // 위의 방법대로 할거면 for문의 외부를 cnt가 아닌 위에 들고온 고객의 정보를 바탕으로 for문을 돌려야 하고(enhanced for문) code에 따라 상품 랜덤(일반이면 당좌 제외 사업자~는 전체랜덤)
                // 계좌 숫자가 너무 많으면 입출금내역의 갯수도 너무 많아질 가능성이 높으니 1인당 요구불예금 계좌로만 생각해서 생성하고 만약 더 넣고 싶다면
                // 위에 사업자인 경우에(if code로 하면됨) 일반예금 하나 당좌예금 하나 만들어 주면 될듯
            }

        }
    }

}
