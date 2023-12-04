package pw.avvero.spring.sandbox.account;

import lombok.Data;

@Data
public class AccountDepositRequest {
    private Integer id;
    private Integer amount;
}
