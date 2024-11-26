package es.merkle.component.repository.adapter;

import es.merkle.component.exception.OrderNotFoundException;
import org.springframework.stereotype.Component;
import es.merkle.component.mapper.OrderMapper;
import es.merkle.component.model.Order;
import es.merkle.component.repository.OrderRepository;
import es.merkle.component.repository.entity.DbOrder;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderAdapter {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public void saveOrder(Order order) {
        DbOrder dbOrder = orderMapper.mapToDbOrder(order);
        orderRepository.save(dbOrder);
    }

    public Order retrieveOrder(String orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::mapToOrder)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
