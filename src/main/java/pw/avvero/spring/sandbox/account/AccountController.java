package pw.avvero.spring.sandbox.account;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("create")
    public CommonResponse createAccount() {
        Account account = accountService.createAccount();
        return new CreateAccountResponse(account.getId());
    }

    @PostMapping("info")
    public CommonResponse getAccountInfo(@RequestBody AccountInfoRequest request) {
        Account account = accountService
                .findById(request.getId())
                .orElse(null);
        if (account != null) {
            return new AccountInfoResponse(account.getId(), account.getBalance());
        } else {
            return new CommonResponse("NotFound");
        }
    }

    @PostMapping("deposit")
    public CommonResponse deposit(@RequestBody AccountDepositRequest request) {
        try {
            accountService.deposit(request.getId(), request.getAmount());
        } catch (AccountNotFoundException e) {
            return new CommonResponse("NotFound");
        }
        return new CommonResponse();
    }

    @PostMapping("withdraw")
    public CommonResponse withdraw(@RequestBody AccountWithdrawalRequest request) {
        try {
            accountService.withdraw(request.getId(), request.getAmount());
        } catch (AccountNotFoundException e) {
            return new CommonResponse("NotFound");
        } catch (NotEnoughMoneyException e) {
            return new CommonResponse("NotEnoughMoney");
        }
        return new CommonResponse();
    }
}
