package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.*;
import at.qe.skeleton.mappers.SubscriptionCreateMapper;
import at.qe.skeleton.mappers.SubscriptionMapper;
import at.qe.skeleton.model.*;
import at.qe.skeleton.services.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {


    private final SubscriptionMapper subscriptionMapper;
    private final SubscriptionService subscriptionService;
    private final SubscriptionCreateMapper subscriptionCreateMapper;


    public SubscriptionController(SubscriptionMapper subscriptionMapper, SubscriptionService subscriptionService, SubscriptionCreateMapper subscriptionCreateMapper) {
        this.subscriptionMapper = subscriptionMapper;
        this.subscriptionService = subscriptionService;
        this.subscriptionCreateMapper = subscriptionCreateMapper;
    }


    @GetMapping("/product/{id}")
    public ResponseEntity<SubscriptionDTO> getSubscriptionByProductId(
            @AuthenticationPrincipal Userx user,
            @PathVariable Long id
    ){
        Subscription subscription = subscriptionService.getSubscriptionByUserAndProduct(user.getId(), id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found for this product"));

        return ResponseEntity.ok(subscriptionMapper.mapTo(subscription));
    }

    @GetMapping("")
    public ResponseEntity<PageResponseDTO<SubscriptionDTO>> getUserSubscriptions(
            @AuthenticationPrincipal Userx user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int limit,
            @RequestParam(required = false, defaultValue = "productId,desc") String sort,
            @RequestParam(required = false) SubscriptionType[] types,
            @RequestParam(required = false) NotificationType[] channels
    ) {
        try {

            Page<Subscription> subscriptionPage = subscriptionService.getUserSubscriptions(
                    user, page, limit, types, channels, sort);

            List<SubscriptionDTO> subscriptionDTOS = subscriptionPage.getContent().stream()
                    .map(subscriptionMapper::mapTo)
                    .toList();

            PageResponseDTO<SubscriptionDTO> response = new PageResponseDTO<>(
                    subscriptionDTOS,
                    page,
                    limit,
                    subscriptionPage.getTotalElements(),
                    subscriptionPage.getTotalPages()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching Subscriptions: " + e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<SubscriptionDTO> createSubscription(
            @AuthenticationPrincipal Userx user,
            @Valid @RequestBody SubscriptionCreateDTO createDTO) {

        Subscription subscription = subscriptionCreateMapper.mapFrom(createDTO);

        SubscriptionDTO response = subscriptionMapper.mapTo(subscriptionService.createSubscription(user, subscription));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> updateSubscription(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionUpdateDTO updateDTO
    ) {
        SubscriptionDTO response = subscriptionMapper.mapTo(subscriptionService.updateSubscription(id, updateDTO));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }

}
