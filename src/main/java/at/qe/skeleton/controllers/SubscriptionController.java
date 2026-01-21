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

/**
 * REST controller acting as the API interface for the Frontend to manage Subscriptions.
 * * <p>It provides functionality to:</p>
 * <ul>
 * <li>Retrieve a user's subscription for a specific product</li>
 * <li>Retrieve all subscriptions belonging to the authenticated user</li>
 * <li>Create a new subscription for a user</li>
 * <li>Update an existing subscription</li>
 * <li>Delete a specific subscription</li>
 * </ul>
 */

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
        // Find the relevant subscription. If it doesn't exist, throw a 404 Not Found error.
        Subscription subscription = subscriptionService.getSubscriptionByUserAndProduct(user.getId(), id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found for this product"));

        // Return 200 OK with the DTO representation of the subscription.
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
            // Retrieve a paginated list of subscriptions for the authenticated user.
            Page<Subscription> subscriptionPage = subscriptionService.getUserSubscriptions(
                    user, page, limit, types, channels, sort);

            // Map Subscription entities to SubscriptionDTOs.
            List<SubscriptionDTO> subscriptionDTOS = subscriptionPage.getContent().stream()
                    .map(subscriptionMapper::mapTo)
                    .toList();

            // Wrap the list and metadata into a PageResponseDTO.
            PageResponseDTO<SubscriptionDTO> response = new PageResponseDTO<>(
                    subscriptionDTOS,
                    page,
                    limit,
                    subscriptionPage.getTotalElements(),
                    subscriptionPage.getTotalPages()
            );

            // Return 200 OK with the paginated response as the payload.
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Propagate internal errors to the Frontend with a 500 status code.
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching Subscriptions: " + e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<SubscriptionDTO> createSubscription(
            @AuthenticationPrincipal Userx user,
            @Valid @RequestBody SubscriptionCreateDTO createDTO) {

        // Map the creation DTO to a Subscription entity.
        Subscription subscription = subscriptionCreateMapper.mapFrom(createDTO);

        // Persist the subscription for the user and map the result back to a DTO.
        SubscriptionDTO response = subscriptionMapper.mapTo(subscriptionService.createSubscription(user, subscription));

        // Return 201 CREATED with the saved subscription DTO.
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SubscriptionDTO> updateSubscription(
            @PathVariable Long id,
            @Valid @RequestBody SubscriptionUpdateDTO updateDTO
    ) {
        // Update the existing subscription and map the result to a DTO.
        SubscriptionDTO response = subscriptionMapper.mapTo(subscriptionService.updateSubscription(id, updateDTO));

        // Return 200 OK with the updated SubscriptionDTO.
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        // Remove the subscription via the service layer.
        subscriptionService.deleteSubscription(id);

        // Return 204 NO CONTENT, the standard response for successful deletion.
        return ResponseEntity.noContent().build();
    }
}