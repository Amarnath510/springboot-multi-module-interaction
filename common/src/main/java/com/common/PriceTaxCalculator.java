package com.common;

public class PriceTaxCalculator {

    final static double TAX_18_PERCENT = 18;
    final static double TAX_28_PERCENT = 28;

    public static Product getProductByObject(Product product) {
        Product newProduct = new Product(product.getId(), product.getName(), product.getPrice());

        if (product.getPrice() <= 5000d) {
            double price = product.getPrice();
            double priceAfterTax = price + ( price * (TAX_18_PERCENT / 100) );
            newProduct.setPrice(priceAfterTax);
        } else {
            double price = product.getPrice();
            double priceAfterTax = price + ( price * (TAX_28_PERCENT / 100) );
            newProduct.setPrice(priceAfterTax);
        }

        return newProduct;
    }

    public static Product getProductById(long id) {
        return getProductByObject( ProductService.getProduct(id) );
    }
}
