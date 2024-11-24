package es.merkle.component.validating2;

import es.merkle.component.model.api.SubmitOrderResponse;
import es.merkle.component.validating2.validator.OrderValidator2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderValidatorRunnerImpl2 implements OrderValidatorRunner2 {

    private final List<OrderValidator2> orderValidator2s;

    @Override
    public void run(SubmitOrderResponse submitOrderResponse) {
        for (int i = orderValidator2s.size() -1; i >= 0; i--) {
            if (orderValidator2s.get(i).validate(submitOrderResponse.getOrder())) {
                orderValidator2s.get(i).updateResponse(submitOrderResponse);
                break;
            }
        }
    }


}
