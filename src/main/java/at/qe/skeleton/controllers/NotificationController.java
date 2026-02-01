package at.qe.skeleton.controllers;

import at.qe.skeleton.dtos.NotificationResponseDTO;
import at.qe.skeleton.dtos.PageResponseDTO;
import at.qe.skeleton.mappers.NotificationResponseMapper;
import at.qe.skeleton.model.*;
import at.qe.skeleton.services.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST controller serving as the API interface for managing user notifications.
 * * <p>This controller provides endpoints to retrieve notifications for the currently
 * authenticated user. It supports filtering by status and channel, providing
 * the data in a paginated format optimized for frontend consumption.</p>
 */

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationResponseMapper notificationResponseMapper;

    public NotificationController(NotificationService notificationService, NotificationResponseMapper notificationResponseMapper) {
        this.notificationService = notificationService;
        this.notificationResponseMapper = notificationResponseMapper;
    }

    /**
     * Retrieves a paginated list of notifications for the authenticated user.
     *
     * <p>Supports optional filtering by notification status and channel.</p>
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - notifications successfully retrieved</li>
     *   <li>400 Bad Request - invalid query parameters</li>
     * </ul>
     *
     * @return paginated list of notifications
     */
    @GetMapping("")
    public ResponseEntity<PageResponseDTO<NotificationResponseDTO>> getUserNotifications(
            @AuthenticationPrincipal Userx user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int limit,
            @RequestParam(required = false, defaultValue = "timestamp,desc") String sort,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false) NotificationType channel
    ) {
        try {
            // Retrieve a paginated list of notifications matching the specified criteria.
            Page<Notification> notificationPage = notificationService.getUserNotifications(
                    user, page, limit, status, channel, sort);

            // Map Notification entities to NotificationResponseDTOs.
            List<NotificationResponseDTO> notificationResponseDTOS = notificationPage.getContent().stream()
                    .map(notificationResponseMapper::mapTo)
                    .toList();

            // Wrap the list and metadata into a PageResponseDTO for structured pagination.
            PageResponseDTO<NotificationResponseDTO> response = new PageResponseDTO<>(
                    notificationResponseDTOS,
                    page,
                    limit,
                    notificationPage.getTotalElements(),
                    notificationPage.getTotalPages()
            );

            // Return 200 OK with the paginated notifications as the payload.
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Propagate internal errors to the Frontend with a 500 status code.
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching notifications: " + e.getMessage());
        }
    }
}