package com.example.kursinisbackend.controllers;

import com.example.kursinisbackend.dto.ErrorResponseDTO;
import com.example.kursinisbackend.dto.SuccessResponseDTO;
import com.example.kursinisbackend.dto.chat.*;
import com.example.kursinisbackend.model.*;
import com.example.kursinisbackend.repos.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
    @Autowired
    private ChatRepo chatRepo;
    @Autowired
    private MessageRepo messageRepo;
    @Autowired
    private OrdersRepo ordersRepo;
    @Autowired
    private BasicUserRepo basicUserRepo;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    @Transactional
    @PostMapping("/order/{orderId}")
    public ResponseEntity<?> getOrCreateChat(@PathVariable int orderId) {
        FoodOrder order = ordersRepo.findById(orderId).orElse(null);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("Order not found", HttpStatus.NOT_FOUND.value()));
        }

        Chat chat = chatRepo.getChatByFoodOrder_Id(orderId);

        if (chat == null) {
            String chatName = "Order #" + orderId + " Chat";
            chat = new Chat(chatName, order);
            chatRepo.save(chat);
        }

        return ResponseEntity.ok(mapChatToDTO(chat));
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<?> getChat(@PathVariable int chatId) {
        Chat chat = chatRepo.findById(chatId).orElse(null);

        if (chat == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("Chat not found", HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(mapChatToDTO(chat));
    }

    @Transactional
    @PostMapping("/messages/{chatId}")
    public ResponseEntity<?> sendMessage(@PathVariable int chatId, @Valid @RequestBody SendMessageRequestDTO dto) {

        Chat chat = chatRepo.findById(chatId).orElse(null);
        if (chat == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("Chat not found", HttpStatus.NOT_FOUND.value()));
        }

        if (chat.getFoodOrder().getOrderStatus() == OrderStatus.COMPLETED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO("Cannot send messages - order is completed",
                            HttpStatus.FORBIDDEN.value()));
        }

        BasicUser sender = basicUserRepo.findById(dto.getSenderId()).orElse(null);
        if (sender == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("Sender not found", HttpStatus.NOT_FOUND.value()));
        }

        FoodOrder order = chat.getFoodOrder();
        boolean isAuthorized = order.getBuyer().getId() == sender.getId() ||
                (order.getDriver() != null && order.getDriver().getId() == sender.getId()) ||
                order.getRestaurant().getId() == sender.getId();

        if (!isAuthorized) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO("You are not authorized to send messages in this chat",
                            HttpStatus.FORBIDDEN.value()));
        }

        Message message = new Message(dto.getText(), sender, chat);
        messageRepo.save(message);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponseDTO("Message sent successfully"));
    }

    @GetMapping("/messages/{chatId}")
    public ResponseEntity<?> getChatMessages(@PathVariable int chatId) {
        Chat chat = chatRepo.findById(chatId).orElse(null);

        if (chat == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("Chat not found", HttpStatus.NOT_FOUND.value()));
        }

        List<MessageResponseDTO> messages = chat.getMessages()
                .stream()
                .map(this::mapMessageToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(messages);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getChatByOrderId(@PathVariable int orderId) {
        FoodOrder order = ordersRepo.findById(orderId).orElse(null);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("Order not found", HttpStatus.NOT_FOUND.value()));
        }

        Chat chat = chatRepo.getChatByFoodOrder_Id(orderId);

        if (chat == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("Chat not found for this order", HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(mapChatToDTO(chat));
    }

    private ChatResponseDTO mapChatToDTO(Chat chat) {
        List<MessageResponseDTO> messages = chat.getMessages()
                .stream()
                .map(this::mapMessageToDTO)
                .collect(Collectors.toList());

        ChatResponseDTO dto = new ChatResponseDTO();
        dto.setId(chat.getId());
        dto.setName(chat.getName());
        dto.setOrderId(chat.getFoodOrder().getId());
        dto.setOrderStatus(chat.getFoodOrder().getOrderStatus().name());
        dto.setDateCreated(chat.getDateCreated().format(dateFormatter));
        dto.setMessages(messages);

        return dto;
    }

    private MessageResponseDTO mapMessageToDTO(Message message) {
        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setId(message.getId());
        dto.setText(message.getText());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getName() + " " + message.getSender().getSurname());
        dto.setSentAt(message.getSentAt().format(dateTimeFormatter));

        return dto;
    }
}