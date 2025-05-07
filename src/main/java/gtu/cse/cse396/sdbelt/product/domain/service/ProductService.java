package gtu.cse.cse396.sdbelt.product.domain.service;

import java.util.List;
import java.util.Optional;

import gtu.cse.cse396.sdbelt.product.domain.model.Product;

/**
 * Service interface for managing product-related operations in the conveyor
 * belt scanning system.
 * <p>
 * This service provides CRUD operations to handle product metadata which may be
 * scanned and logged
 * during the belt process. Implementations of this interface are responsible
 * for interacting with
 * the persistence layer or invoking domain logic.
 */
public interface ProductService {

    /**
     * Creates a new product entry with the given name and description.
     *
     * @param id          the long id of the product; must not be null
     * @param name        the name of the product; must not be null or empty
     * @param description a brief description of the product; can be null or empty
     * @param imageId     the ID of the product's image; can be null or empty
     */
    void create(Long id, String name, String description, String imageId);

    /**
     * Updates an existing product identified by the given ID.
     *
     * @param id          the long id of the product to update; must not be null
     * @param name        the new name of the product; must not be null or empty
     * @param description the new description of the product; can be null or empty
     * @param imageId     the new ID of the product's image; can be null or empty
     * @throws IllegalArgumentException if the product does not exist
     */
    void update(Long id, String name, String description, String imageId);

    /**
     * Deletes the product identified by the given ID.
     *
     * @param id the long id of the product to delete; must not be null
     * @throws IllegalArgumentException if the product does not exist
     */
    void delete(Long id);

    /**
     * Retrieves the product with the specified ID.
     *
     * @param id the long id of the product; must not be null
     * @return the product entity
     * @throws IllegalArgumentException if the product does not exist
     */
    Product get(Long id);

    /**
     * Attempts to find a product with the specified ID.
     *
     * @param id the long id of the product; must not be null
     * @return an {@link Optional} containing the product if found, or empty if not
     */
    Optional<Product> find(Long id);

    /**
     * Lists all available products in the system.
     *
     * @return a list of all product entities; never null
     */
    List<Product> list();
}
