package es.merkle.component.controller;


import es.merkle.component.service.OrderService;
import es.merkle.component.model.Order;
import es.merkle.component.model.api.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "order-service")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    @ResponseBody
    public Order create(@RequestBody CreateOrderRequest order) {
        return orderService.createOrder(order);
    }

    @PostMapping("/submit")
    @ResponseBody
    public SubmitOrderResponse submit(@RequestBody SubmitOrderRequest order) {
        return orderService.submitOrder(order);
    }

    @PostMapping("/modify")
    @ResponseBody
    public Order modify(@RequestBody ModifyOrderRequest order) {
        return orderService.modifyOrder(order);
    }
}
