package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.PaymentRequestDTO;
import at.qe.skeleton.dtos.PaymentResponseDTO;
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
        

        if ("0000-0000-0000-0000".equals(paymentRequest.cardNumber()) || 
            "0000000000000000".equals(paymentRequest.cardNumber())) {
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
        
        return ResponseEntity.ok(response);
    }
}
