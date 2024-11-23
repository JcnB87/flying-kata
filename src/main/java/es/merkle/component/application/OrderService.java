package es.merkle.component.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import es.merkle.component.mapper.OrderMapper;
import es.merkle.component.model.Order;
import es.merkle.component.model.OrderStatus;
import es.merkle.component.model.api.CreateOrderRequest;
import es.merkle.component.model.api.SubmitOrderRequest;
import es.merkle.component.model.api.SubmitOrderResponse;
import es.merkle.component.populating.PopulatorRunner;
import es.merkle.component.repository.adapter.OrderAdapter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final PopulatorRunner populatorRunner;
    private final OrderMapper orderMapper;
    private final OrderAdapter orderAdapter;

    public Order createOrder(CreateOrderRequest orderRequest) {
        Order order = mapCreateOrderRequest(orderRequest);

        try {
            populateOrder(order);
            saveOrder(order);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return order;
    }

    public SubmitOrderResponse submitOrder(SubmitOrderRequest submitOrderRequest) {
        Order order = new Order(); // TODO: Idk how to do this
        SubmitOrderResponse submitOrderResponse = new SubmitOrderResponse();
        submitOrderResponse.setOrder(order);

        if (order.getStatus() == OrderStatus.INVALID) {
            submitOrderResponse.getOrder().setStatus(OrderStatus.FAILED);
            submitOrderResponse.setMessage("The order was not submitted because it's INVALID");
            return submitOrderResponse;
        } else if (order.getStatus() == OrderStatus.VALID) {
            submitOrderResponse.setMessage("The order was submitted successfully");
            submitOrderResponse.getOrder().setStatus(OrderStatus.SUBMITTED);
        } else if (order.getStatus() == OrderStatus.NEW) {
            submitOrderResponse.setMessage("The was not submitted because it's not in a final status");
            submitOrderResponse.getOrder().setStatus(OrderStatus.FAILED);
        } else {
            return null;
        }

        return submitOrderResponse;
    }

    private void saveOrder(Order order) {
        orderAdapter.saveOrder(order);
    }

    private void populateOrder(Order order) {
        populatorRunner.run(order);
    }

    private Order mapCreateOrderRequest(CreateOrderRequest orderRequest) {
        return orderMapper.mapCreateOrderRequestToOrder(orderRequest);
    }
}
