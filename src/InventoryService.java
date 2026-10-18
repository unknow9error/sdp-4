import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class InventoryService {
    private final Map<String, Integer> stock = new HashMap<>();
    private final Map<String, BigDecimal> prices = Map.of(
            "notebook", new BigDecimal("1200.00"),
            "pen", new BigDecimal("300.00"));
    private final Map<String, Cart> reservations = new HashMap<>();

    public InventoryService() {
        stock.put("notebook", 10);
        stock.put("pen", 20);
    }

    public BigDecimal reserve(String orderId, Cart cart) {
        if (reservations.containsKey(orderId)) throw new OrderException("Duplicate reservation");
        BigDecimal total = BigDecimal.ZERO;
        // Validate the entire cart before changing any stock.
        for (var item : cart.quantities().entrySet()) {
            if (!prices.containsKey(item.getKey())
                    || available(item.getKey()) < item.getValue()) {
                throw new OrderException("Insufficient stock: " + item.getKey());
            }
            total = total.add(prices.get(item.getKey())
                    .multiply(BigDecimal.valueOf(item.getValue())));
        }
        cart.quantities().forEach((sku, quantity) -> stock.put(sku, stock.get(sku) - quantity));
        reservations.put(orderId, cart);
        return total;
    }

    public void release(String orderId) {
        Cart cart = reservations.remove(orderId);
        if (cart != null) {
            cart.quantities().forEach((sku, quantity) -> stock.put(sku, stock.get(sku) + quantity));
        }
    }

    public int available(String sku) { return stock.getOrDefault(sku, 0); }
}
