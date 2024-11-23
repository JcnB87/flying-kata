package es.merkle.component.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String id;
    private String name;
    private ProductStatus productStatus;
    private ProductCategory productCategory;
    private BigDecimal price;
    private LocalDate expiringDate;
    private LocalDate releasedDate;
}
