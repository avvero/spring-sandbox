package pw.avvero.spring.sandbox.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse implements Serializable {

    private String resultCode = "Ok";

}
