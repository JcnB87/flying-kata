package es.merkle.component.validating2.validator;

import es.merkle.component.model.Order;
import es.merkle.component.model.OrderStatus;
import es.merkle.component.model.api.SubmitOrderResponse;
import org.springframework.stereotype.Component;

@Component
public class InvalidOrderStatusValidator implements OrderValidator2 {

    @Override
    public boolean validate(Order order) {
        return order.getStatus() == OrderStatus.INVALID;
    }

    @Override
    public void updateResponse(SubmitOrderResponse submitOrderResponse) {
        submitOrderResponse.getOrder().setStatus(OrderStatus.FAILED);
        submitOrderResponse.setMessage("The order was not submitted because it's INVALID");

    }
}
