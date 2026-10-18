import java.math.BigDecimal;
import java.util.Map;

public class FacadeChecks {
    private static int passed;
    private static int total;
    private static final Customer CUSTOMER =
            new Customer("Demo Student", "student@example.invalid", "Demo address");
    private record Fixture(InventoryService inventory, PaymentProcessor payment,
                           ShippingService shipping, NotificationService notification,
                           OrderFacade facade) { }

    private static Fixture fixture(boolean shippingAvailable) {
        var inventory = new InventoryService();
        var payment = new PaymentProcessor();
        var shipping = new ShippingService(shippingAvailable);
        var notification = new NotificationService();
        return new Fixture(inventory, payment, shipping, notification,
                new OrderFacade(inventory, payment, shipping, notification));
    }

    private static void check(String name, boolean result) {
        total++;
        if (result) passed++;
        System.out.println((result ? "PASS " : "FAIL ") + name);
    }

    private static boolean rejected(Runnable action) {
        try { action.run(); return false; }
        catch (OrderException | IllegalArgumentException expected) { return true; }
    }

    public static void main(String[] args) {
        var f = fixture(true);
        var cart = new Cart(Map.of("notebook", 2, "pen", 3));
        var placed = f.facade().placeOrder(cart, CUSTOMER, OrderFacade.PaymentChoice.ACCEPTED);
        check("successful order total and stock", placed.total().compareTo(new BigDecimal("3300.00")) == 0
                && f.inventory().available("notebook") == 8 && f.inventory().available("pen") == 17
                && f.payment().isCharged(placed.orderId()) && f.shipping().isScheduled(placed.orderId())
                && f.notification().messages().size() == 1);
        var cancelled = f.facade().cancelOrder(placed.orderId());
        check("cancel restores stock, payment and shipment", cancelled.status().equals("CANCELLED")
                && f.inventory().available("notebook") == 10 && f.inventory().available("pen") == 20
                && !f.payment().isCharged(placed.orderId()) && !f.shipping().isScheduled(placed.orderId()));
        f.facade().cancelOrder(placed.orderId());
        check("repeated cancellation does not release twice", f.inventory().available("notebook") == 10
                && f.notification().messages().size() == 2);
        check("declined payment releases reservation", rejected(() -> f.facade().placeOrder(
                cart, CUSTOMER, OrderFacade.PaymentChoice.DECLINED))
                && f.inventory().available("notebook") == 10 && f.inventory().available("pen") == 20
                && !f.payment().isCharged("ORDER-2") && !f.shipping().isScheduled("ORDER-2")
                && f.notification().messages().size() == 2);
        var failedShipping = fixture(false);
        check("shipping failure refunds payment and releases stock", rejected(() -> failedShipping.facade()
                .placeOrder(cart, CUSTOMER, OrderFacade.PaymentChoice.ACCEPTED))
                && failedShipping.inventory().available("notebook") == 10
                && !failedShipping.payment().isCharged("ORDER-1")
                && !failedShipping.shipping().isScheduled("ORDER-1")
                && failedShipping.notification().messages().isEmpty());
        check("insufficient stock changes no item", rejected(() -> f.facade().placeOrder(
                new Cart(Map.of("notebook", 1, "pen", 100)), CUSTOMER,
                OrderFacade.PaymentChoice.ACCEPTED))
                && f.inventory().available("notebook") == 10 && f.inventory().available("pen") == 20);
        check("unknown order rejected", rejected(() -> f.facade().cancelOrder("missing")));
        check("empty cart rejected", rejected(() -> new Cart(Map.of())));
        check("invalid customer rejected", rejected(() -> new Customer("", "bad", "")));
        check("negative quantity rejected", rejected(() -> new Cart(Map.of("pen", -1))));
        System.out.println("SUMMARY: " + passed + "/" + total + " PASS");
        if (passed != total) System.exit(1);
    }
}
