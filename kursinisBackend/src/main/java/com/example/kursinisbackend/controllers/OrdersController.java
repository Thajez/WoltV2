package com.example.kursinisbackend.controllers;

import com.example.kursinisbackend.dto.ErrorResponseDTO;
import com.example.kursinisbackend.dto.SuccessResponseDTO;
import com.example.kursinisbackend.dto.restaurant.*;
import com.example.kursinisbackend.model.*;
import com.example.kursinisbackend.repos.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/orders")
public class OrdersController {
    @Autowired
    private OrdersRepo ordersRepo;
    @Autowired
    private CuisineRepo cuisineRepo;
    @Autowired
    private RestaurantRepo restaurantRepo;
    @Autowired
    private DriverRepo driverRepo;
    @Autowired
    private BasicUserRepo basicUserRepo;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    @GetMapping("/restaurants")
    public ResponseEntity<List<RestaurantResponseDTO>> getAllRestaurants() {
        List<RestaurantResponseDTO> list =
                restaurantRepo.findAll()
                .stream()
                .map(r -> new RestaurantResponseDTO(
                        r.getId(),
                        r.getName(),
                        r.getSurname(),
                        r.getPhoneNumber(),
                        r.getAddress(),
                        r.getTypeOfRestaurant(),
                        r.getOpeningTime(),
                        r.getClosingTime()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    @GetMapping("/menu/{id}")
    public ResponseEntity<List<CuisineResponseDTO>> getRestaurantMenu(@PathVariable int id) {
        List<CuisineResponseDTO> menu = cuisineRepo.getCuisineByRestaurantId(id)
                .stream()
                .map(c -> new CuisineResponseDTO(
                        c.getId(),
                        c.getName(),
                        c.getIngredients(),
                        c.getPrice(),
                        c.isSpicy(),
                        c.isVegan()
                )).collect(Collectors.toList());

        return ResponseEntity.ok(menu);
    }

    @Transactional
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequestDTO dto) {

        BasicUser client = basicUserRepo.findById(dto.getBuyerId()).orElse(null);
        Restaurant restaurant = restaurantRepo.findById(dto.getRestaurantId()).orElse(null);

        if (client == null || restaurant == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Invalid user or restaurant", HttpStatus.BAD_REQUEST.value()));
        }

        FoodOrder order = new FoodOrder(0.0, client, restaurant);
        order.setDateCreated(LocalDate.now());
        order.setDateUpdated(LocalDate.now());
        order.setOrderStatus(OrderStatus.PENDING);

        List<FoodOrderItem> items = new ArrayList<>();
        double totalPrice = 0.0;

        for (var item : dto.getItems()) {
            Cuisine cuisine = cuisineRepo.findById(item.getCuisineId()).orElse(null);
            if (cuisine == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponseDTO("Cuisine not found: " + item.getCuisineId(), HttpStatus.BAD_REQUEST.value()));
            }

            double price = cuisine.getPrice() * item.getQuantity();
            totalPrice += price;

            FoodOrderItem orderItem = new FoodOrderItem(order, cuisine, item.getQuantity(), price);
            items.add(orderItem);
        }

        order.setItems(items);
        order.setPrice(totalPrice);

        ordersRepo.save(order);
        return ResponseEntity.ok(new SuccessResponseDTO("Order created successfully with ID: " + order.getId()));
    }

    @PutMapping("/assignDelivery/{driverId}/{orderId}")
    public ResponseEntity<?> assignOrderToDriver(@PathVariable int driverId, @PathVariable int orderId) {
        FoodOrder order = ordersRepo.findById(orderId).orElse(null);
        Driver driver = driverRepo.findById(driverId).orElse(null);

        if (order == null || driver == null || (order.getOrderStatus() != OrderStatus.STARTED && order.getOrderStatus() != OrderStatus.READY)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Order not available for assignment", HttpStatus.BAD_REQUEST.value()));
        }

        order.setDriver(driver);
        ordersRepo.save(order);

        return ResponseEntity.ok(new SuccessResponseDTO("Order assigned to driver"));
    }

    @PutMapping("/startDelivery/{driverId}/{orderId}")
    public ResponseEntity<?> startDelivery(@PathVariable int driverId, @PathVariable int orderId) {
        FoodOrder order = ordersRepo.findById(orderId).orElse(null);

        if (order == null || order.getDriver() == null || order.getDriver().getId() != driverId
                || order.getOrderStatus() != OrderStatus.READY) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Order is not ready yet!", HttpStatus.BAD_REQUEST.value()));
        }

        order.setOrderStatus(OrderStatus.IN_DELIVERY);
        order.setDateUpdated(LocalDate.now());

        ordersRepo.save(order);
        return ResponseEntity.ok(new SuccessResponseDTO("Succesfully started delivery"));
    }

    @PutMapping("/completeDelivery/{driverId}/{orderId}")
    public ResponseEntity<?> completeDelivery(@PathVariable int driverId, @PathVariable int orderId) {
        FoodOrder order = ordersRepo.findById(orderId).orElse(null);

        if (order == null || order.getDriver() == null || order.getDriver().getId() != driverId
                || order.getOrderStatus() != OrderStatus.IN_DELIVERY) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Order is not in delivery!", HttpStatus.BAD_REQUEST.value()));
        }

        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setDateUpdated(LocalDate.now());

        ordersRepo.save(order);
        return ResponseEntity.ok(new SuccessResponseDTO("Succesfully completed delivery"));
    }

    @GetMapping("/clientOrders/{clientId}")
    public ResponseEntity<List<OrderResponseDTO>> getClientActiveOrders(@PathVariable int clientId) {
        List<OrderResponseDTO> orders = ordersRepo.getFoodOrdersByBuyer_Id(clientId)
                .stream()
                .filter(o ->
                        o.getOrderStatus() != OrderStatus.COMPLETED
                )
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/clientHistory/{clientId}")
    public ResponseEntity<List<OrderResponseDTO>> getClientOrderHistory(@PathVariable int clientId) {
        List<OrderResponseDTO> orders = ordersRepo.getFoodOrdersByBuyer_Id(clientId)
                .stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.COMPLETED)
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/driverDeliveries/{driverId}")
    public ResponseEntity<List<OrderResponseDTO>> getDriverActiveDeliveries(@PathVariable int driverId) {
        List<OrderResponseDTO> orders = ordersRepo.getFoodOrdersByDriver_Id(driverId)
                .stream()
                .filter(o -> o.getOrderStatus() != OrderStatus.COMPLETED)
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/driverHistory/{driverId}")
    public ResponseEntity<List<OrderResponseDTO>> getDriverDeliveryHistory(@PathVariable int driverId) {
        List<OrderResponseDTO> orders = ordersRepo.getFoodOrdersByDriver_Id(driverId)
                .stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.COMPLETED)
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/available")
    public ResponseEntity<List<OrderResponseDTO>> getAvailableOrders() {
        List<OrderResponseDTO> orders = ordersRepo.findAll()
                .stream()
                .filter(o ->
                        o.getDriver() == null &&
                        (o.getOrderStatus() == OrderStatus.STARTED || o.getOrderStatus() == OrderStatus.READY))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    private OrderResponseDTO mapToDTO(FoodOrder order) {
        List<OrderItemResponseDTO> items = order.getItems()
                .stream()
                .map(i -> new OrderItemResponseDTO(
                        i.getId(),
                        i.getQuantity(),
                        i.getCuisine().getId(),
                        i.getTotalPrice(),
                        i.getCuisine().getName()
                ))
                .collect(Collectors.toList());

        Integer driverId = order.getDriver() != null ?
                order.getDriver().getId() : null;

        String driverName = order.getDriver() != null ?
                order.getDriver().getName() : null;

        String driverSurname = order.getDriver() != null ?
                order.getDriver().getSurname() : null;

        return new OrderResponseDTO(
                order.getId(),
                order.getPrice(),
                order.getBuyer().getId(),
                order.getBuyer().getName(),
                order.getBuyer().getSurname(),
                order.getRestaurant().getId(),
                order.getRestaurant().getName(),
                driverId,
                driverName,
                driverSurname,
                order.getRestaurant().getSurname(),
                order.getOrderStatus(),
                items,
                order.getDateCreated().format(formatter),
                order.getDateUpdated().format(formatter)
        );
    }
}
