package gtu.cse.cse396.sdbelt.product.infra.adapter;

import gtu.cse.cse396.sdbelt.product.domain.service.ProductService;
import gtu.cse.cse396.sdbelt.product.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductDataInitializer implements ApplicationRunner {

    static final String[] DEFAULT_PRODUCT_NAMES = {
            "Elma", "Pet Şişe", "Kalem", "Silgi", "Kutu"
    };
    static final String[] DEFAULT_PRODUCT_DESCRIPTIONS = {
            "Elma açıklaması", "Pet Şişe açıklaması", "Kalem açıklaması", "Silgi açıklaması", "Kutu açıklaması"
    };

    static final String[] DEFAULT_PRODUCT_IMAGE_IDS = {
            "apple.png", "plastic-bottle.png", "pencil.png", "eraser.png", "box.png"
    };

    private final ProductService productService;

    @Override
    public void run(ApplicationArguments args) {
        List<Product> existingProducts = productService.list();

        if (existingProducts.size() >= 5) {
            System.out.println("Database already initialized with at least 5 products.");
            return;
        }

        long nextId = existingProducts.stream()
                .mapToLong(Product::id)
                .max()
                .orElse(0L) + 1;

        for (int i = 0; i < 5 - existingProducts.size(); i++) {
            long productId = nextId + i;
            String name = DEFAULT_PRODUCT_NAMES[i % DEFAULT_PRODUCT_NAMES.length];
            String description = DEFAULT_PRODUCT_DESCRIPTIONS[i % DEFAULT_PRODUCT_DESCRIPTIONS.length];
            String imageId = DEFAULT_PRODUCT_IMAGE_IDS[i % DEFAULT_PRODUCT_IMAGE_IDS.length];
            productService.create(productId, name, description, imageId);
            System.out.println("Inserted product: " + name);
        }

        System.out.println("Product initialization completed.");
    }
}
