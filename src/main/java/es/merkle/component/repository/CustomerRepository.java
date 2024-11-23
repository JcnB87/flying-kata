package es.merkle.component.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import es.merkle.component.repository.entity.DbCustomer;

@Repository
public interface CustomerRepository extends CrudRepository<DbCustomer, String> {
}
