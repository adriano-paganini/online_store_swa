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

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationResponseMapper notificationResponseMapper;

    public NotificationController(NotificationService notificationService, NotificationResponseMapper notificationResponseMapper) {
        this.notificationService = notificationService;
        this.notificationResponseMapper = notificationResponseMapper;
    }

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
            Page<Notification> notificationPage = notificationService.getUserNotifications(
                    user, page, limit, status, channel, sort);

            List<NotificationResponseDTO> notificationResponseDTOS = notificationPage.getContent().stream()
                    .map(notificationResponseMapper::mapTo)
                    .toList();

            PageResponseDTO<NotificationResponseDTO> response = new PageResponseDTO<>(
                    notificationResponseDTOS,
                    page,
                    limit,
                    notificationPage.getTotalElements(),
                    notificationPage.getTotalPages()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching notifications: " + e.getMessage());
        }
    }
}





