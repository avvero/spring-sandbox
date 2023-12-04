package pw.avvero.spring.sandbox.account;

public class NotEnoughMoneyException extends Exception {
    public NotEnoughMoneyException(Integer id, Integer amount) {
        super(String.format("There is no enough money on account %s: %s", id, amount));
    }
}
