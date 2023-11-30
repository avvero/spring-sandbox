package pw.avvero.spring.sandbox.account;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account createAccount() {
        return accountRepository.save(Account.builder().balance(0).build());
    }

    public Optional<Account> findById(Integer id) {
        return accountRepository.findById(id);
    }

    @Transactional
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 10, backoff = @Backoff(delay = 200))
    public void deposit(Integer id, Integer amount) throws AccountNotFoundException {
        log.debug("Attempt to deposit {} on account {}", amount, id);
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
    }

    @Transactional
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 10, backoff = @Backoff(delay = 200))
    public void withdraw(Integer id, Integer amount) throws AccountNotFoundException, NotEnoughMoneyException {
        log.debug("Attempt to withdraw {} from account {}", amount, id);
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        if (account.getBalance() - amount < 0) {
            throw new NotEnoughMoneyException(id, amount);
        }
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);
    }

}
