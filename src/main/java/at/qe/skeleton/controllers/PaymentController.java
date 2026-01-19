package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.PaymentRequestDTO;
import at.qe.skeleton.dtos.PaymentResponseDTO;
import at.qe.skeleton.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/cart/payment")
public class PaymentController {


    private final OrderService orderService;

    public PaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> processPayment(@Valid @RequestBody PaymentRequestDTO paymentRequest) {

        
        if (paymentRequest.amount() <= 0) {
            PaymentResponseDTO response = new PaymentResponseDTO(
                    false,
                    null,
                    "Invalid payment amount",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Handle funny payment methods
        if ("netflix_password".equalsIgnoreCase(paymentRequest.paymentMethod())) {
            return handleNetflixPasswordPayment(paymentRequest);
        }
        
        if ("dad_joke".equalsIgnoreCase(paymentRequest.paymentMethod())) {
            return handleDadJokePayment(paymentRequest);
        }

        // Validate card number for regular credit card payments
        String cardNumber = paymentRequest.cardNumber();
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            PaymentResponseDTO response = new PaymentResponseDTO(
                    false,
                    null,
                    "Payment declined: Card number is required",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if ("0000-0000-0000-0000".equals(cardNumber) || 
            "0000000000000000".equals(cardNumber)) {
            PaymentResponseDTO response = new PaymentResponseDTO(
                    false,
                    null,
                    "Payment declined: Invalid card",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
   
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        PaymentResponseDTO response = new PaymentResponseDTO(
                true,
                transactionId,
                "Payment processed successfully",
                LocalDateTime.now()
        );

        // TODO: accept orderId
        // Order confirmedOrder = orderService.confirmPayment(orderId);
        return ResponseEntity.ok(response);
    }
    
    private ResponseEntity<PaymentResponseDTO> handleNetflixPasswordPayment(PaymentRequestDTO paymentRequest) {
        String password = paymentRequest.cardNumber(); // Using cardNumber field to store the password
        
        if (password == null || password.trim().isEmpty()) {
            PaymentResponseDTO response = new PaymentResponseDTO(
                    false,
                    null,
                    "Payment declined: Netflix password is required. We promise we'll only watch one episode... maybe two.",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Funny password validation
        if (password.length() < 4) {
            PaymentResponseDTO response = new PaymentResponseDTO(
                    false,
                    null,
                    "Payment declined: That password is too short. Even we have standards! Try again with a longer password.",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Check for common passwords (funny rejection messages)
        String lowerPassword = password.toLowerCase();
        if (lowerPassword.contains("password") || lowerPassword.contains("1234") || lowerPassword.equals("admin")) {
            PaymentResponseDTO response = new PaymentResponseDTO(
                    false,
                    null,
                    "Payment declined: Really? That's your password? Please use a better password.",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Success! But with a funny message
        String transactionId = "NETFLIX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PaymentResponseDTO response = new PaymentResponseDTO(
                true,
                transactionId,
                "Payment processed successfully! Your Netflix password has been... borrowed. We promise we'll only watch Real Housewives of Beverly Hills. Again. For the 47th time.",
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }
    
    private ResponseEntity<PaymentResponseDTO> handleDadJokePayment(PaymentRequestDTO paymentRequest) {
        String joke = paymentRequest.cardHolderName(); // Using cardHolderName field to store the joke
        
        if (joke == null || joke.trim().isEmpty()) {
            PaymentResponseDTO response = new PaymentResponseDTO(
                    false,
                    null,
                    "Payment declined: A dad joke is required. Come on, you can do better than that!",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        String lowerJoke = joke.toLowerCase().trim();
        
        // Check if it's actually a joke (contains question/answer pattern or common joke words)
        boolean hasQuestion = lowerJoke.contains("?") || lowerJoke.contains("why") || lowerJoke.contains("what") || lowerJoke.contains("how");
        boolean hasJokeWords = lowerJoke.contains("joke") || lowerJoke.contains("dad") || lowerJoke.contains("pun");
        
        if (!hasQuestion && !hasJokeWords && lowerJoke.length() < 30) {
            PaymentResponseDTO response = new PaymentResponseDTO(
                    false,
                    null,
                    "Payment declined: That doesn't sound like a dad joke. Try harder! Remember: the cornier, the better!",
                    LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Check for quality indicators (dad joke patterns)
        boolean isQualityJoke = lowerJoke.contains("i'm") || lowerJoke.contains("i am") || 
                                lowerJoke.contains("because") || lowerJoke.contains("dad") ||
                                lowerJoke.contains("hi ") || lowerJoke.contains("hello ");
        
        // Success with funny messages based on joke quality
        String transactionId = "DAD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String message;
        
        if (isQualityJoke) {
            message = "Payment processed successfully! That was a quality dad joke. We're groaning with approval.";
        } else {
            message = "Payment processed successfully! Your joke was... acceptable. We've seen worse.";
        }
        
        PaymentResponseDTO response = new PaymentResponseDTO(
                true,
                transactionId,
                message,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }
}
