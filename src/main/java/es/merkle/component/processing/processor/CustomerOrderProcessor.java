package es.merkle.component.processing.processor;

import es.merkle.component.model.Order;
import es.merkle.component.model.OrderType;
import es.merkle.component.model.Product;
import es.merkle.component.repository.adapter.ProductAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
@RequiredArgsConstructor
public class CustomerOrderProcessor implements OrderProcessor {

    private final ProductAdapter productAdapter;

    @Override
    public boolean accepts(Order order) {
        return order.getOrderType() == OrderType.ADD || order.getOrderType() == OrderType.REMOVE;
    }

    @Override
    public void process(Order order) {
        if (order.getOrderType() == OrderType.ADD) {
            Product productOrder = productAdapter.getProduct(order.getProcessingProductId());
            order.getAddingProducts().add(productOrder);

            BigDecimal finalPrice = order.getAddingProducts().stream()
                    .map(Product::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
//                .reduce(BigDecimal.ZERO, (acc, price) -> acc.add(price));
            order.setFinalPrice(finalPrice);
        } else if (order.getOrderType() == OrderType.REMOVE) {
            Product productOrder = productAdapter.getProduct(order.getProcessingProductId());
            order.getRemoveProducts().add(productOrder);

            // Update AddingProducts by removing products that are in removeProducts
            order.getAddingProducts().removeAll(order.getRemoveProducts());

            BigDecimal finalPrice = order.getAddingProducts().stream()
                    .map(Product::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setFinalPrice(finalPrice);
        }
    }

}
