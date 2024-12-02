package es.merkle.component.submit.validating.validator;

import es.merkle.component.model.Order;
import es.merkle.component.model.OrderStatus;
import es.merkle.component.model.api.SubmitOrderResponse;
import org.springframework.stereotype.Component;

@Component
public class SubmitSingleOrderValidator implements SingleOrderValidator {

    @Override
    public void validateAndUpdateResponse(SubmitOrderResponse submittedOrderResponse) {

        Order order = submittedOrderResponse.getOrder();
        OrderStatus status = order.getStatus();

        if (status == null) {
            throw new IllegalStateException("Unknown order status: null");
        }

        switch (status) {
            case INVALID:
                order.setStatus(OrderStatus.FAILED);
                submittedOrderResponse.setMessage("The order was not submitted because it's INVALID");
                break;
            case VALID:
                submittedOrderResponse.setMessage("The order was submitted successfully");
                submittedOrderResponse.getOrder().setStatus(OrderStatus.SUBMITTED);
                break;
            case FAILED:
                submittedOrderResponse.setMessage("The order was failed");
                break;
            case NEW:
                order.setStatus(OrderStatus.FAILED);
                submittedOrderResponse.setMessage("The order was not submitted because it's not in a final status");
                break;
            case SUBMITTED:
                submittedOrderResponse.setMessage("The order was already submitted");
                break;
        }
    }
}
