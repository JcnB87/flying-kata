package es.merkle.component.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.merkle.component.model.Customer;
import es.merkle.component.model.Order;
import es.merkle.component.model.Product;
import es.merkle.component.model.api.CreateOrderRequest;
import es.merkle.component.model.api.ModifyOrderRequest;
import es.merkle.component.model.api.SubmitOrderRequest;
import es.merkle.component.repository.entity.DbOrder;

@Mapper(componentModel = "spring", imports = {UUID.class})
public abstract class OrderMapper {

    ObjectMapper objectMapper = new ObjectMapper();

    @Mapping(target = "id", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "status", constant = "NEW")
    public abstract Order mapCreateOrderRequestToOrder(CreateOrderRequest orderRequest);

    @Mapping(source = "orderId", target = "id")
    public abstract Order mapModifyOrderRequestToOrder(ModifyOrderRequest orderRequest);
    public abstract Order mapSubmitOrderRequestToOrder(SubmitOrderRequest submitOrderRequest);

    public abstract DbOrder mapToDbOrder(Order order);

    public abstract Order mapToOrder(DbOrder order);

    protected Customer mapCustomer(String customer) {
        try {
            return objectMapper.readValue(customer, Customer.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String mapCustomer(Customer customer) {
        try {
            return objectMapper.writeValueAsString(customer);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected List<String> mapProductsToIds(List<Product> products) {
        return Optional.ofNullable(products)
                .orElse(new ArrayList<>())
                .stream()
                .map(Product::getId)
                .collect(Collectors.toList());
    }

    protected List<Product> mapIdsToProducts(List<String> productIds) {
        return Optional.ofNullable(productIds)
                .orElse(new ArrayList<>())
                .stream()
                .map(productId -> new Product()) // TODO: I need to map the product from the db here
                .collect(Collectors.toList());
    }
}
