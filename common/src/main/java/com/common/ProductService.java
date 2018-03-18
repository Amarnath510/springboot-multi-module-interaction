package com.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductService {
    private static List<Product> products = new ArrayList<>();

    // To initialize all the products.
    static {
        if (products == null || products.isEmpty()) {
            for (long i = 1; i <= 5; i++) {
                Product p = new Product(i, "Product-" + i, 1000d * i);
                products.add(p);
            }
        }
    }

    public static List<Product> getProducts() {
        return products;
    }

    public static boolean createProduct(Product product) {
        try {
            product.setId(products.size() + 1);
            products.add(product);
        } catch(Exception e) {
            return false;
        }
        return true;
    }

    public static Product getProduct(long id) {
        Optional<Product> product = ProductService.getProducts()
                .stream()
                .filter(p -> p.getId() == id)
                .findFirst();
        return product.get();
    }
}
