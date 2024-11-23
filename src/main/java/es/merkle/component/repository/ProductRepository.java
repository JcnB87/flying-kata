package es.merkle.component.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import es.merkle.component.repository.entity.DbProduct;

@Repository
public interface ProductRepository extends CrudRepository<DbProduct, String> {
}
