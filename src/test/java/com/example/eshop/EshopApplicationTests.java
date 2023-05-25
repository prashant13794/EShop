package com.example.eshop;

import com.example.eshop.domain.basket.Basket;
import com.example.eshop.domain.discount.BuyXGetYDiscount;
import com.example.eshop.domain.discount.Discount;
import com.example.eshop.domain.product.Product;
import com.example.eshop.exceptions.ProductOutOfStockException;
import com.example.eshop.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = EshopApplication.class)
@AutoConfigureMockMvc
class EshopApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductService productService;

    private final String basketsAPI = "/api/baskets/";
    private final String discountsAPI = "/api/discounts/";
    private final String productsAPI = "/api/products/";

    @Test
    void contextLoads() throws Exception {
        Product product = new Product("name1", new BigDecimal(10), 1);
        productService.save(product);

        mvc.perform(get(productsAPI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo((result) -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("name1"));
    }

    @Test
    void canSaveAndDeleteProduct() throws Exception {
        Product product = new Product("name1", new BigDecimal(10), 1);
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult createProductResult = basicAssertion(createProduct(product, objectMapper))
                .andReturn();
        Long id = objectMapper.readValue(createProductResult.getResponse().getContentAsString(), Product.class).getId();

        basicAssertion(getProduct(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("name1"));

        removeProduct(id)
                .andReturn();

        try {
            getProduct(id).andReturn();
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getCause().getClass());
        }
    }


    @Test
    void canSaveDiscount() throws Exception {
        Discount discount = new BuyXGetYDiscount(3, 2);
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult createDiscountResult = basicAssertion(createDiscount(discount, objectMapper))
                .andReturn();
        Long id = objectMapper.readValue(createDiscountResult.getResponse().getContentAsString(), Discount.class).getId();

        basicAssertion(mvc.perform(get(discountsAPI + id)
                .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.buy").value("3"));
    }

    @Test
    void canAddDiscountToProduct() throws Exception {
        Discount discount = new BuyXGetYDiscount(3, 2);
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult createDiscountResult = basicAssertion(createDiscount(discount, objectMapper))
                .andReturn();
        Long discountId = objectMapper.readValue(createDiscountResult.getResponse().getContentAsString(), Discount.class).getId();
        Product product = new Product("name1", new BigDecimal(10), 1);

        MvcResult createProductResult = createProduct(product, objectMapper).andReturn();
        Long productId = objectMapper.readValue(createProductResult.getResponse().getContentAsString(), Product.class).getId();

        basicAssertion(addDiscountToProduct(discountId, productId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.discounts[0].buy").value("3"));

        basicAssertion(addDiscountToProduct(discountId, productId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.discounts[0].buy").value("3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.discounts[1].buy").value("3"));
    }


    @Test
    void canAddRemoveProductInBasket() throws Exception {
        Basket basket = new Basket();
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult createBasketResult = basicAssertion(createBasket(basket, objectMapper))
                .andReturn();
        Long basketId = objectMapper.readValue(createBasketResult.getResponse().getContentAsString(), Basket.class).getId();

        Discount discount = new BuyXGetYDiscount(3, 2);

        MvcResult createDiscountResult = createDiscount(discount, objectMapper)
                .andDo((response) -> System.out.println(response.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        Long discountId = objectMapper.readValue(createDiscountResult.getResponse().getContentAsString(), Discount.class).getId();

        Product product = new Product("name1", new BigDecimal(10), 2);

        MvcResult createProductResult = createProduct(product, objectMapper).andReturn();
        Long productId = objectMapper.readValue(createProductResult.getResponse().getContentAsString(), Product.class).getId();

        basicAssertion(addDiscountToProduct(discountId, productId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.discounts[0].buy").value("3"));

        basicAssertion(addProductToBasket(basketId, productId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.basketItems[0].product.name").value("name1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.basketItems[0].quantity").value(1));

        basicAssertion(addProductToBasket(basketId, productId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.basketItems[0].quantity").value(2));

        Product anotherProduct = new Product("name2", new BigDecimal(15), 5);

        MvcResult createAnotherProductResult = createProduct(anotherProduct, objectMapper).andReturn();
        Long anotherProductId = objectMapper.readValue(createAnotherProductResult.getResponse().getContentAsString(), Product.class).getId();

        basicAssertion(addProductToBasket(basketId, anotherProductId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.basketItems[0].product.name").value("name1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.basketItems[0].quantity").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.basketItems[1].product.name").value("name2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.basketItems[1].quantity").value(1));

        basicAssertion(getBasket(basketId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.basketItems.length()").value(2));

        basicAssertion(removeProductFromBasket(basketId, anotherProductId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.basketItems[0].product.name").value("name1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.basketItems[0].quantity").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.basketItems.length()").value(1));

        basicAssertion(getProduct(anotherProductId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stock").value("5"));


        basicAssertion(removeProductFromBasket(basketId, productId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.basketItems[0].product.name").value("name1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.basketItems[0].quantity").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.basketItems.length()").value(1));
        basicAssertion(getProduct(productId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stock").value("1"));
    }

    @Test
    void canCalculateTotalOfBasketWithDiscounts() throws Exception {
        Basket basket = new Basket();
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult createBasketResult = basicAssertion(createBasket(basket, objectMapper))
                .andReturn();
        Long basketId = objectMapper.readValue(createBasketResult.getResponse().getContentAsString(), Basket.class).getId();

        Discount discount = new BuyXGetYDiscount(3, 2);

        MvcResult createDiscountResult = createDiscount(discount, objectMapper)
                .andDo((response) -> System.out.println(response.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        Long discountId = objectMapper.readValue(createDiscountResult.getResponse().getContentAsString(), Discount.class).getId();

        Product product = new Product("name1", new BigDecimal(10), 5);

        MvcResult createProductResult = createProduct(product, objectMapper).andReturn();
        Long productId = objectMapper.readValue(createProductResult.getResponse().getContentAsString(), Product.class).getId();

        addDiscountToProduct(discountId, productId);

        addProductToBasket(basketId, productId);
        addProductToBasket(basketId, productId);
        addProductToBasket(basketId, productId);
        addProductToBasket(basketId, productId);
        addProductToBasket(basketId, productId);

        Product anotherProduct = new Product("name2", new BigDecimal(15), 5);

        MvcResult createAnotherProductResult = createProduct(anotherProduct, objectMapper).andReturn();
        Long anotherProductId = objectMapper.readValue(createAnotherProductResult.getResponse().getContentAsString(), Product.class).getId();

        addProductToBasket(basketId, anotherProductId);

        getBasket(basketId)
                .andExpect(MockMvcResultMatchers.jsonPath("$.basketItems.length()").value(2));

        basicAssertion(mvc.perform(get(basketsAPI + basketId + "/total")
                .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(45.00));

        mvc.perform(get(basketsAPI + basketId + "/receipt")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());

    }

    @Test
    public void testConcurrentUsage() throws Exception {
        Basket basket = new Basket();
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult createBasketResult = basicAssertion(createBasket(basket, objectMapper))
                .andReturn();
        Long basketId = objectMapper.readValue(createBasketResult.getResponse().getContentAsString(), Basket.class).getId();

        Product product = new Product("name1", new BigDecimal(10), 1);

        MvcResult createProductResult = createProduct(product, objectMapper).andReturn();
        Long productId = objectMapper.readValue(createProductResult.getResponse().getContentAsString(), Product.class).getId();

        ExecutorService es = Executors.newFixedThreadPool(2);
        Future<?> future1 = es.submit(() -> {
            addProductToBasket(basketId, productId);
            return null;
        });
        Future<?> future2 = es.submit(() -> {
            addProductToBasket(basketId, productId);

            return null;
        });

        try {
            future1.get();
            future2.get();
        } catch (ExecutionException e) {
            assertEquals(ProductOutOfStockException.class, e.getCause().getCause().getClass());
        }
    }

    private ResultActions getBasket(Long basketId) throws Exception {
        return mvc.perform(get(basketsAPI + basketId)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions removeProductFromBasket(Long basketId, Long productId) throws Exception {
        return mvc.perform(delete(String.format(basketsAPI + "%s/products/%s", basketId, productId))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions addProductToBasket(Long basketId, Long productId) throws Exception {
        return mvc.perform(post(String.format(basketsAPI + "%s/products/%s", basketId, productId))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions basicAssertion(ResultActions resultActions) throws Exception {
        return resultActions.andDo((response) -> System.out.println(response.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    private ResultActions createBasket(Basket basket, ObjectMapper objectMapper) throws Exception {
        return mvc.perform(post(basketsAPI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(basket)));
    }


    private ResultActions createDiscount(Discount discount, ObjectMapper objectMapper) throws Exception {
        return mvc.perform(post(discountsAPI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(discount)));
    }

    private ResultActions getProduct(Long id) throws Exception {
        return mvc.perform(get(productsAPI + id)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions createProduct(Product product, ObjectMapper objectMapper) throws Exception {
        return mvc.perform(post(productsAPI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)));
    }

    private ResultActions removeProduct(Long id) throws Exception {
        return mvc.perform(delete(productsAPI + id)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions addDiscountToProduct(Long discountId, Long productId) throws Exception {
        return mvc.perform(post(String.format(productsAPI + "%s/discounts/%s", productId, discountId))
                .contentType(MediaType.APPLICATION_JSON));
    }

}
