package com.example.prif233.Utils;

public class Constants {
    public static final String HOME_URL = "http://10.0.2.2:8080/";
    // ========== USER ENDPOINTS (UserController) ==========
    public static final String VALIDATE_USER_URL = HOME_URL + "api/users/login";
    public static final String CREATE_BASIC_USER_URL = HOME_URL + "api/users/basic";
    public static final String CREATE_DRIVER_USER_URL = HOME_URL + "api/users/driver";
    public static final String UPDATE_BASIC_USER_URL = HOME_URL + "api/users/update/basic/";
    public static final String UPDATE_DRIVER_USER_URL = HOME_URL + "api/users/update/driver/";
    public static final String UPDATE_PASSWORD_URL = HOME_URL + "api/users/password/";
    public static final String DELETE_USER_URL = HOME_URL + "api/users/delete/";
    public static final String GET_USER_BY_ID = HOME_URL + "api/users/";

    // ========== ORDER ENDPOINTS (OrdersController) ==========
    public static final String GET_ALL_RESTAURANTS_URL = HOME_URL + "api/orders/restaurants";
    public static final String GET_RESTAURANT_MENU = HOME_URL + "api/orders/menu/";
    public static final String CREATE_ORDER = HOME_URL + "api/orders/create";
    public static final String ASSIGN_DELIVERY_URL = HOME_URL + "api/orders/assignDelivery/";
    public static final String START_DELIVERY_URL = HOME_URL + "api/orders/startDelivery/";
    public static final String COMPLETE_DELIVERY_URL = HOME_URL + "api/orders/completeDelivery/";
    public static final String GET_CLIENT_ACTIVE_ORDERS = HOME_URL + "api/orders/clientOrders/";
    public static final String GET_CLIENT_ORDER_HISTORY = HOME_URL + "api/orders/clientHistory/";
    public static final String GET_DRIVER_ACTIVE_DELIVERIES = HOME_URL + "api/orders/driverDeliveries/";
    public static final String GET_DRIVER_DELIVERY_HISTORY = HOME_URL + "api/orders/driverHistory/";
    public static final String GET_AVAILABLE_ORDERS = HOME_URL + "api/orders/available";


    // ========== CHAT ENDPOINTS (ChatController) ==========
    public static final String GET_OR_CREATE_CHAT = HOME_URL + "api/chats/order/";
    public static final String GET_CHAT = HOME_URL + "api/chats/";
    public static final String SEND_MESSAGE = HOME_URL + "api/chats/messages/";
    public static final String GET_CHAT_MESSAGES = HOME_URL + "api/chats/messages/";
    public static final String GET_MESSAGES_BY_ORDER = HOME_URL + "api/chats/order/";

    // ========== REVIEW ENDPOINTS (ReviewController) ==========
    public static final String CREATE_REVIEW = HOME_URL + "api/reviews/create";
    public static final String GET_REVIEWS_BY_OWNER = HOME_URL + "api/reviews/owner/";
    public static final String GET_REVIEWS_BY_TARGET = HOME_URL + "api/reviews/target/";
    public static final String GET_REVIEWS_FOR_ORDER = HOME_URL + "api/reviews/order/";
    public static final String GET_AVERAGE_RATING = HOME_URL + "api/reviews/average/";
}
