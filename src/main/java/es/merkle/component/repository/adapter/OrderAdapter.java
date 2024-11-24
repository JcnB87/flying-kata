package es.merkle.component.repository.adapter;

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
        DbOrder dbOrder = orderRepository.findById(orderId).orElseThrow();
        return orderMapper.mapToOrder(dbOrder);
    }
}
