package gtu.cse.cse396.sdbelt.product.infra.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import gtu.cse.cse396.sdbelt.product.domain.model.Product;
import gtu.cse.cse396.sdbelt.product.domain.service.ProductService;

public class ProductAdapter implements ProductService {

    @Override
    public void create(String name, String description) {
        // Implementation for creating a product
    }

    @Override
    public void update(UUID id, String name, String description) {
        // Implementation for updating a product
    }

    @Override
    public void delete(UUID id) {
        // Implementation for deleting a product
    }

    @Override
    public Product get(UUID id) {
        // Implementation for getting a product by ID
        return null;
    }

    @Override
    public Optional<Product> find(UUID id) {
        // Implementation for finding a product by ID
        return Optional.empty();
    }

    @Override
    public List<Product> list() {
        // Implementation for listing all products
        return null;
    }
}
