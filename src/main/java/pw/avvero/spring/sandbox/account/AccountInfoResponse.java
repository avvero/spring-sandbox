package pw.avvero.spring.sandbox.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountInfoResponse extends CommonResponse {

    private Integer id;
    private Integer balance;

}
