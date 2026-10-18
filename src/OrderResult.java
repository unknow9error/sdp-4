import java.math.BigDecimal;

public record OrderResult(String orderId, BigDecimal total,
                          String trackingNumber, String status) { }
