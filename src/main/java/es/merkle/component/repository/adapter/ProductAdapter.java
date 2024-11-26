package es.merkle.component.repository.adapter;

import es.merkle.component.exception.ProductNotFoundException;
import es.merkle.component.mapper.ProductMapper;
import es.merkle.component.model.Product;
import es.merkle.component.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductAdapter {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Product getProduct(String id) {
        return productRepository.findById(id)
                .map(productMapper::mapToProduct)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
