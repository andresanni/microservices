package com.andresanni.productservice;

import com.andresanni.productservice.dto.ProductRequest;
import com.andresanni.productservice.dto.ProductResponse;
import com.andresanni.productservice.model.Product;
import com.andresanni.productservice.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ProductRepository productRepository;
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	@DirtiesContext
	void shouldCreateProduct() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString))
				.andExpect(status().isCreated());

		Assertions.assertEquals(1, productRepository.findAll().size());
	}

	@Test
	void shouldReturnAllProducts() throws Exception{

		List<ProductResponse> expectedProducts = new ArrayList<ProductResponse>();
		expectedProducts.add(new ProductResponse("iphone 12", "Iphone 12","Descripcion", BigDecimal.valueOf(1000)));
		expectedProducts.add(new ProductResponse("iphone 14", "Iphone 14","Descripcion", BigDecimal.valueOf(2000)));


		productRepository.saveAll(Arrays.asList(
				new Product("iphone 12", "Iphone 12","Descripcion", BigDecimal.valueOf(1000)),
				new Product("iphone 14", "Iphone 14","Descripcion", BigDecimal.valueOf(2000))
		));

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
				.andExpect(status().isOk())
				.andReturn();

		String responseBody = result.getResponse().getContentAsString();
		List<ProductResponse> actualProducts = objectMapper.readValue(responseBody, new TypeReference<List<ProductResponse>>() {});
		Assertions.assertEquals(expectedProducts,actualProducts);
	}

	private ProductRequest getProductRequest(){
		return ProductRequest.builder()
				.name("iPhone 13")
				.description("Iphone 13")
				.price(BigDecimal.valueOf(1200))
				.build();
	}


}
