import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PaymentProcessor {
    public enum Decision { APPROVE, DECLINE }
    private final Map<String, BigDecimal> charges = new HashMap<>();

    public void charge(String orderId, BigDecimal amount, Decision decision) {
        if (amount.signum() <= 0) throw new OrderException("Amount must be positive");
        if (decision == Decision.DECLINE) throw new OrderException("Payment declined");
        if (charges.putIfAbsent(orderId, amount) != null) {
            throw new OrderException("Order is already charged");
        }
    }

    public void refund(String orderId) { charges.remove(orderId); }
    public boolean isCharged(String orderId) { return charges.containsKey(orderId); }
}
