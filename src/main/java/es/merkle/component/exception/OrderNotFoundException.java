package es.merkle.component.exception;

public class OrderNotFoundException extends PopulatorException {
    public OrderNotFoundException(String orderId) {
        super(orderId);
    }
}
