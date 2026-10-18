import java.util.Map;
import java.util.Objects;

public record Cart(Map<String, Integer> quantities) {
    public Cart {
        Objects.requireNonNull(quantities, "Cart is required");
        if (quantities.isEmpty()) throw new IllegalArgumentException("Cart is empty");
        for (var entry : quantities.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank()
                    || entry.getValue() == null || entry.getValue() <= 0) {
                throw new IllegalArgumentException("Each item needs a positive quantity");
            }
        }
        quantities = Map.copyOf(quantities);
    }
}
