package com.andresanni.orderservice.service;

import com.andresanni.orderservice.dto.InventoryResponse;
import com.andresanni.orderservice.dto.OrderLineItemdsDto;
import com.andresanni.orderservice.dto.OrderRequest;
import com.andresanni.orderservice.model.Order;
import com.andresanni.orderservice.model.OrderLineItems;
import com.andresanni.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;


    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems =
                orderRequest.getOrderLineItemsDtoList().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                                .map(OrderLineItems::getSkuCode)
                                .collect(Collectors.toList());

        //Call to inventory service, and place order if product is in stock
            InventoryResponse[] inventoryResponses = webClient.get()
                        .uri("http://localhost:8082/api/inventory",
                                uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                        .retrieve()
                        .bodyToMono(InventoryResponse[].class)
                        .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponses).allMatch(inventoryResponse -> inventoryResponse.isInStock());

        if(allProductsInStock){
            orderRepository.save(order);
        }else{
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }

    }

    private OrderLineItems mapToDto(OrderLineItemdsDto orderLineDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineDto.getPrice());
        orderLineItems.setQuantity(orderLineDto.getQuantity());
        orderLineItems.setSkuCode(orderLineDto.getSkuCode());
        return orderLineItems;
    }

}
