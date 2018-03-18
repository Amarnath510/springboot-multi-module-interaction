package com.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.common.Product;
import com.common.ProductService;

@RestController
@SpringBootApplication(scanBasePackages = {"com", "hello"})
@RequestMapping("/search")
public class SearchApplication {

    public static final String PRICING_URL_TAX = "http://localhost:8200/pricing/tax";
    public static final String PRICING_URL_HELLO = "http://localhost:8200/pricing/hello";
    public static final String PRICING_URL_TAX_ID = "http://localhost:8200/pricing/taxbyid/{id}";
    private RestTemplate restTemplate = new RestTemplate();

    /*
        Simple GET call to test this application.
     */
    @RequestMapping("/hello")
    @GetMapping
    public String hello() {
        return "Search: Hello";
    }

    /*
        Simple GET call to see whether we are able return a class as JSON.
     */
    @RequestMapping(value = "/product",
            method = RequestMethod.GET,
            produces = {"application/json"})
    public Product product() {
        return new Product(1, "Laptop", 45000d);
    }

    /*
        Get call using a Path param and return a Product as JSON.
     */
    @RequestMapping(value = "/product/{id}",
            method = RequestMethod.GET,
            produces = {"application/json"})
    public Product productWith(@PathVariable("id") long id) {
        return ProductService.getProduct(id);
    }

    /*
        GET to return list of Products as JSON.
     */
    @RequestMapping(value = "/products",
            method = RequestMethod.GET,
            produces = {"application/json"})
    public List<Product> products() {
        return ProductService.getProducts();
    }

    /*
        POST call to insert a new Product.
     */
    @RequestMapping(value = "/product/create",
            method = RequestMethod.POST,
            consumes = {"application/json"}
    )
    public String createProduct(@RequestBody Product product) {
        if (ProductService.createProduct(product)) {
            return "Created Product = " + product.toString();
        }

        return "Creation failed for Product = " + product.toString();
    }

    /*
        Search for a product using id.
        Get the product from Products.
        Once we got the product get the product cost from Pricing service.
     */
    @RequestMapping(value = "/productwithtax/{id}",
                    method = RequestMethod.GET,
                    produces = {"application/json"})
    public Product productWithTaxToPricingProduct(@PathVariable("id") long id) {
        Product productBeforeTax = ProductService.getProduct(id);

        // TEST Call: // restTemplate.getForObject(PRICING_URL_HELLO, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> entity = new HttpEntity(productBeforeTax, headers);
        ResponseEntity<Product> responseEntity = restTemplate.exchange(PRICING_URL_TAX, HttpMethod.POST, entity, Product.class);
        return responseEntity.getBody();
    }

    /*
        Gives product id to pricing and it does the rest of the task.
        This is same as "/productwithtax/{id}" call but in a more simple sense.
     */
    @RequestMapping(value = "/productwithtaxanother/{id}",
            method = RequestMethod.GET,
            produces = {"application/json"})
    public Product productWithTaxToPricingProductId(@PathVariable("id") long id) {
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);
        Product product = restTemplate.getForObject(PRICING_URL_TAX_ID, Product.class, params);
        return product;
    }

    /**
     * Runs Spring Boot Module.
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }
}