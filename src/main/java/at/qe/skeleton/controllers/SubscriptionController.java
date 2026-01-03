package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.SubscriptionCreateDTO;
import at.qe.skeleton.dtos.SubscriptionDTO;
import at.qe.skeleton.dtos.SubscriptionUpdateDTO;
import at.qe.skeleton.mappers.SubscriptionMapper;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.services.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {


    private final SubscriptionMapper subscriptionMapper;
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionMapper subscriptionMapper, SubscriptionService subscriptionService) {
        this.subscriptionMapper = subscriptionMapper;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubscriptionDTO[]> getUserSubscriptions(
            @AuthenticationPrincipal Userx user) {
        SubscriptionDTO[] dtos = Arrays.stream(subscriptionService.loadUserSubscriptions(user))
                .map(subscriptionMapper::mapTo)
                .toArray(SubscriptionDTO[]::new);

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubscriptionDTO> createSubscription(
            @AuthenticationPrincipal Userx user, // Use your custom User entity directly if possible
            @Valid @RequestBody SubscriptionCreateDTO createDTO) {

        SubscriptionDTO response = subscriptionMapper.mapTo(subscriptionService.createSubscription(user, createDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SubscriptionDTO> updateSubscription(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionUpdateDTO updateDTO
    ) {
        SubscriptionDTO response = subscriptionMapper.mapTo(subscriptionService.updateSubscription(id, updateDTO));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }

}
