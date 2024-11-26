package es.merkle.component.submit.validating.validator;

import es.merkle.component.model.Order;
import es.merkle.component.model.OrderStatus;
import es.merkle.component.model.api.SubmitOrderResponse;
import org.springframework.stereotype.Component;

@Component
public class ValidOrderStatusValidator implements OrderValidator2 {
    @Override
    public boolean validate(Order order) {
        return order.getStatus() == OrderStatus.VALID;
    }

    @Override
    public void updateResponse(SubmitOrderResponse submitOrderResponse) {
        submitOrderResponse.setMessage("The order was submitted successfully");
        submitOrderResponse.getOrder().setStatus(OrderStatus.SUBMITTED);
    }
}
