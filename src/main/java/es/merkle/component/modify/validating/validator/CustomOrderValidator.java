package es.merkle.component.modify.validating.validator;

import es.merkle.component.model.Order;
import es.merkle.component.model.OrderStatus;
import es.merkle.component.model.Product;
import es.merkle.component.model.ProductStatus;
import es.merkle.component.repository.adapter.ProductAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CustomOrderValidator implements OrderValidator {

    private final ProductAdapter productAdapter;

    @Override
    public boolean validate(Order order) {
        Product product = productAdapter.getProduct(order.getProcessingProductId());
        LocalDate localDateNow = LocalDate.now();

        // Check if the product is invalid based on the criteria
        return product.getProductStatus() == ProductStatus.NOT_AVAILABLE ||
                product.getReleasedDate().isAfter(localDateNow) ||
                product.getExpiringDate().isBefore(localDateNow);
    }

    @Override
    public void setStatus(Order order, OrderStatus orderStatus) {
        order.setStatus(orderStatus);
    }
}
