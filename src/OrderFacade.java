import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrderFacade {
    public enum PaymentChoice { ACCEPTED, DECLINED }
    private final InventoryService inventory;
    private final PaymentProcessor payments;
    private final ShippingService shipping;
    private final NotificationService notifications;
    private final Map<String, StoredOrder> orders = new HashMap<>();
    private int sequence;

    private record StoredOrder(Customer customer, OrderResult result) { }

    public OrderFacade() {
        this(new InventoryService(), new PaymentProcessor(),
                new ShippingService(), new NotificationService());
    }

    // Composition root for advanced clients and controlled failure tests.
    public OrderFacade(InventoryService inventory, PaymentProcessor payments,
                       ShippingService shipping, NotificationService notifications) {
        this.inventory = Objects.requireNonNull(inventory);
        this.payments = Objects.requireNonNull(payments);
        this.shipping = Objects.requireNonNull(shipping);
        this.notifications = Objects.requireNonNull(notifications);
    }

    public OrderResult placeOrder(Cart cart, Customer customer, PaymentChoice choice) {
        Objects.requireNonNull(cart);
        Objects.requireNonNull(customer);
        Objects.requireNonNull(choice);
        String id = "ORDER-" + ++sequence;
        try {
            BigDecimal total = inventory.reserve(id, cart);
            payments.charge(id, total, choice == PaymentChoice.ACCEPTED
                    ? PaymentProcessor.Decision.APPROVE : PaymentProcessor.Decision.DECLINE);
            String tracking = shipping.schedule(id, customer);
            OrderResult result = new OrderResult(id, total, tracking, "PLACED");
            orders.put(id, new StoredOrder(customer, result));
            notifications.record(customer, result);
            return result;
        } catch (OrderException failure) {
            compensate(id);
            throw new OrderException("Cannot place order: " + failure.getMessage(), failure);
        }
    }

    public OrderResult cancelOrder(String orderId) {
        StoredOrder order = orders.get(orderId);
        if (order == null) throw new OrderException("Unknown order: " + orderId);
        if (order.result().status().equals("CANCELLED")) return order.result();
        compensate(orderId);
        OrderResult result = new OrderResult(orderId, order.result().total(), "", "CANCELLED");
        orders.put(orderId, new StoredOrder(order.customer(), result));
        notifications.record(order.customer(), result);
        return result;
    }

    private void compensate(String orderId) {
        shipping.cancel(orderId);
        payments.refund(orderId);
        inventory.release(orderId);
    }
}
