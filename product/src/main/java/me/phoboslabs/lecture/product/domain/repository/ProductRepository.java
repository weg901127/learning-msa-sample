package me.phoboslabs.lecture.product.domain.repository;

import me.phoboslabs.lecture.product.domain.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {
}
