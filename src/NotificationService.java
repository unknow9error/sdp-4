import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private final List<String> messages = new ArrayList<>();

    public void record(Customer customer, OrderResult order) {
        messages.add(customer.email() + " | " + order.orderId() + " | " + order.status());
    }

    public List<String> messages() { return List.copyOf(messages); }
}
