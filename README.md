# Spring Boot - Multi application Modules interaction
- How search, pricing (new module) will interact using their API's and share common library.

**NOTE:** If imports(Spring Boot classes) does not work then just refresh the project root by doing --> Goto tasks (on the right most panel, select gradle) --> Right click on root project --> Regresh Gradle Project + Refresh Dependencies.

# Setup
- Create project with name **springboot-multi-module-interaction**.
**Refer:** [springboot-multi-module](https://github.com/Amarnath510/springboot_multi_module) to setup multi module application using gradle.
- Setup common and search modules using this above reference.
- Run and check them before porceeding further.

# Pricing Module (New Module, apart from common, search modules)
- Add new module `pricing`. 
  Right click on root project --> New --> Module --> (Gradle + Java) --> give `pricing` as ArtifactId --> Finish
- Copy `search/build.gradle` to `pricing/build.gradle`. Just update the baseName.
- Pricing API is same as search API.
- Create src under pricing. Add main/java under src. Add a package `com.pricing`.
- Add class `PricingApplication` and add the following code.
```java
package com.pricing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/pricing")
@SpringBootApplication(scanBasePackages = "com")
public class PricingApplication {
    @RequestMapping("/hello")
    public String sayHello() { return "Pricing: Hello World!!!"; }
    public static void main(String[] args) { SpringApplication.run(PricingApplication.class, args); }
}
```
- Add `resource/application.properites` directory under src/main. Set property **server.port=8200**.

# Run (Check whether we are able to hit /search/* and /pricing/* APIs)
- Build & Run, `./gradlew build && ./gradlew :search:bootRun :pricing:bootRun`
- Hit, http://localhost:8100/search/hello && http://localhost:8200/pricing/hello

# Add API's to Pricing module
```java
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
```
- Build & Run, `./gradlew build && ./gradlew :search:bootRun :pricing:bootRun`
- Check, http://localhost:8200/pricing/taxbyid/1.
- Use Postman and Check, POST: http://localhost:8200/pricing/tax && BODY: raw & type as JSON && 
```java
// Input
{
    "id": 1,
    "name": "Product-1",
    "price": 1000
}

// Output:
{
    "id": 1,
    "name": "Product-1",
    "price": 1180
}
```

# Add API's to Search (which in-turn calls Pricing)
- When we search for a particular product by passing its `id` and want to know its value after applying tax then we use the following code,
```java
public static final String PRICING_URL_TAX = "http://localhost:8200/pricing/tax";

@RequestMapping(value = "/productwithtax/{id}",
                    method = RequestMethod.GET,
                    produces = {"application/json"})
    public Product productWithTaxToPricingProduct(@PathVariable("id") long id) {
        Product productBeforeTax = ProductService.getProduct(id);
        // TEST Call: restTemplate.getForObject(PRICING_URL_HELLO, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> entity = new HttpEntity(productBeforeTax, headers);
        ResponseEntity<Product> responseEntity = restTemplate.exchange(PRICING_URL_TAX, HttpMethod.POST, entity, Product.class);
        return responseEntity.getBody();
    }
```
As `pricing/tax` consumes JSON with `@RequestBody` as `Product` we need to clearly mention that we are passing a JSON object of type product in the headers as above.
NOTE: /pricing/tax is a POST call because it consumes object. I tried with GET and it is not consuming hence throwing error. Also before making any call to pricing check whether you are able to call pricing API directly.

- Similarly we also have another API in `/pricing` as `/taxbyid/{id}` which is a GET call and takes only id as param. So in search service we write the following call to check pricing.
```java
public static final String PRICING_URL_TAX_ID = "http://localhost:8200/pricing/taxbyid/{id}";

@RequestMapping(value = "/productwithtaxanother/{id}",
            method = RequestMethod.GET,
            produces = {"application/json"})
    public Product productWithTaxToPricingProductId(@PathVariable("id") long id) {
        Map<String, Long> params = new HashMap<>();
        params.put("id", id);
        Product product = restTemplate.getForObject(PRICING_URL_TAX_ID, Product.class, params);
        return product;
    }
```
NOTE: Here pricing call is a simple GET call so we need not add any `headers` or `entities` etc.
- Build & Run: ./gradlew build && ./gradlew :search:bootRun :pricing:bootRun
- Check, http://localhost:8100/search/productwithtax/1
- Check, http://localhost:8100/search/productwithtaxanother/2

# Conclusion
- We have created new service(module) `pricing` and have written API's to interact `search` with `pricing`.