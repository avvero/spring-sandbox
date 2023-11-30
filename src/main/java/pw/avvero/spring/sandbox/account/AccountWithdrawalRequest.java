package pw.avvero.spring.sandbox.account;

import lombok.Data;

@Data
public class AccountWithdrawalRequest {

    private Integer id;
    private Integer amount;

}
