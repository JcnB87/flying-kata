package es.merkle.component.exception;

public class ProductNotFoundException extends PopulatorException {
    public ProductNotFoundException(String productId) {
        super(productId);
    }
}
