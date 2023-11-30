package pw.avvero.spring.sandbox.account;

public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(Integer id) {
        super(String.format("Account %s is not found", id));
    }
}
