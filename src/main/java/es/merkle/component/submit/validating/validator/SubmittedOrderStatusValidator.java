package es.merkle.component.submit.validating.validator;

import es.merkle.component.model.Order;
import es.merkle.component.model.OrderStatus;
import es.merkle.component.model.api.SubmitOrderResponse;
import org.springframework.stereotype.Component;

@Component
public class SubmittedOrderStatusValidator implements OrderValidator2 {
    @Override
    public boolean validate(Order order) {
        return order.getStatus() == OrderStatus.SUBMITTED;
    }

    @Override
    public void updateResponse(SubmitOrderResponse submitOrderResponse) {
        submitOrderResponse.setMessage("The order was already submitted");
    }
}