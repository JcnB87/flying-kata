package es.merkle.component.application;

import es.merkle.component.model.*;
import es.merkle.component.model.api.*;
import es.merkle.component.processing.OrderProcessorRunner;
import es.merkle.component.validating.OrderValidatorRunner;
import es.merkle.component.validating2.OrderValidatorRunner2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import es.merkle.component.mapper.OrderMapper;
import es.merkle.component.populating.PopulatorRunner;
import es.merkle.component.repository.adapter.OrderAdapter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final PopulatorRunner populatorRunner;
    private final OrderProcessorRunner orderProcessorRunner;
    private final OrderValidatorRunner orderValidatorRunner;
    private final OrderValidatorRunner2 orderValidatorRunner2;
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
        Order order = mapSubmitOrderRequestToOrder(submitOrderRequest);
        Order retrievedOrder = retrieveOrder(order);

        SubmitOrderResponse submitOrderResponse = mapOrderToSubmitOrderResponse(retrievedOrder);

        validateOrderResponse(submitOrderResponse);

        return submitOrderResponse;
    }

    private SubmitOrderResponse mapOrderToSubmitOrderResponse(Order retrievedOrder) {
        return orderMapper.mapSubmitOrderResponseToOrder(retrievedOrder);
    }

    public Order modifyOrder(ModifyOrderRequest modifyOrderRequest) {
        Order requestedOrder = mapModifyOrderRequest(modifyOrderRequest);
        Order retrievedOrder = retrieveOrder(requestedOrder);
        updateRetrievedOrder(requestedOrder, retrievedOrder);
        processOrder(retrievedOrder);
        validateOrder(retrievedOrder);
        saveOrder(retrievedOrder);

        return retrievedOrder;
    }

    private Order mapModifyOrderRequest(ModifyOrderRequest modifyOrderRequest) {
        return orderMapper.mapModifyOrderRequestToOrder(modifyOrderRequest);
    }

    private Order mapSubmitOrderRequestToOrder(SubmitOrderRequest submitOrderRequest) {
        return orderMapper.mapSubmitOrderRequestToOrder(submitOrderRequest);
    }

    private Order retrieveOrder(Order order) {
        return orderAdapter.retrieveOrder(order.getId());
    }

    private void updateRetrievedOrder(Order requestedOrder, Order retrievedOrder) {
        orderMapper.updateOrder(requestedOrder, retrievedOrder);
    }

    private void processOrder(Order order) {
        orderProcessorRunner.run(order);
    }

    private void validateOrder(Order order) {
        orderValidatorRunner.run(order);
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

    private void validateOrderResponse(SubmitOrderResponse submitOrderResponse) {
        orderValidatorRunner2.run(submitOrderResponse);
    }

}
