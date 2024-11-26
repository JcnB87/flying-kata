package es.merkle.component.submit.validating.validator;

import es.merkle.component.model.Order;
import es.merkle.component.model.OrderStatus;
import es.merkle.component.model.api.SubmitOrderResponse;
import org.springframework.stereotype.Component;

@Component
public class NewOrderStatusValidator implements OrderValidator2 {


    @Override
    public boolean validate(Order order) {
        return order.getStatus() == OrderStatus.NEW;
    }

    @Override
    public void updateResponse(SubmitOrderResponse submitOrderResponse) {
        submitOrderResponse.setMessage("The order was not submitted because it's not in a final status");
        submitOrderResponse.getOrder().setStatus(OrderStatus.FAILED);
    }

}
