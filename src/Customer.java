public record Customer(String name, String email, String address) {
    public Customer {
        if (name == null || name.isBlank() || email == null
                || !email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")
                || address == null || address.isBlank()) {
            throw new IllegalArgumentException("Valid name, email and address are required");
        }
    }
}
