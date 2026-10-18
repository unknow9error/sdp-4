import java.util.HashMap;
import java.util.Map;

public class ShippingService {
    private final boolean available;
    private final Map<String, String> shipments = new HashMap<>();

    public ShippingService() { this(true); }
    public ShippingService(boolean available) { this.available = available; }

    public String schedule(String orderId, Customer customer) {
        if (!available) throw new OrderException("Shipping unavailable");
        if (shipments.containsKey(orderId)) throw new OrderException("Shipment already exists");
        String tracking = "DEMO-SHIP-" + orderId;
        shipments.put(orderId, customer.address());
        return tracking;
    }

    public void cancel(String orderId) { shipments.remove(orderId); }
    public boolean isScheduled(String orderId) { return shipments.containsKey(orderId); }
}
