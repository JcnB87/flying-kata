package es.merkle.component.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Customer {
    private String id;
    private String name;
    private String address;
    private String phoneNumber;
    private final List<Product> ownedProducts = new ArrayList<>();
}
