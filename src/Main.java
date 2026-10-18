import java.util.Map;

public class Main {
    public static void main(String[] args) {
        OrderFacade shop = new OrderFacade();
        Customer customer = new Customer("Demo Student", "student@example.invalid", "Demo address");
        Cart cart = new Cart(Map.of("notebook", 2, "pen", 3));
        System.out.println("LOCAL SIMULATION - no real payment, shipment or email");
        OrderResult placed = shop.placeOrder(cart, customer, OrderFacade.PaymentChoice.ACCEPTED);
        System.out.println(placed);
        System.out.println(shop.cancelOrder(placed.orderId()));
        try {
            shop.placeOrder(cart, customer, OrderFacade.PaymentChoice.DECLINED);
        } catch (OrderException expected) {
            System.out.println(expected.getMessage());
        }
    }
}
