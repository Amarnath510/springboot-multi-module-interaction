package com.pricing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.common.PriceTaxCalculator;
import com.common.Product;

@RestController
@SpringBootApplication(scanBasePackages = "com")
@RequestMapping("/pricing")
public class PricingApplication {

    @RequestMapping("/hello")
    @GetMapping
    public String hello() {
        return "Pricing: Hello";
    }


    @RequestMapping(value = "/tax",
                    method = { RequestMethod.POST },
                    consumes = {"application/json"}
                    )
    public Product getProductAfterTaxByObject(@RequestBody Product product) {
        return PriceTaxCalculator.getProductByObject(product);
    }

    @RequestMapping(value = "/taxbyid/{id}",
            method = { RequestMethod.GET },
            produces = {"application/json"}
    )
    public Product getPriceAfterTaxById(@PathVariable("id") long id) {
        System.out.println("Pricing = " + id);
        return PriceTaxCalculator.getProductById(id);
    }

    /**
     * Runs Spring Boot Module.
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(PricingApplication.class, args);
    }
}
