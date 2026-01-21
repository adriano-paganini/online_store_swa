package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.NotificationResponseDTO;
import at.qe.skeleton.model.Notification;
import org.springframework.stereotype.Service;

/**
 * Mapper for converting {@link Notification} entities into {@link NotificationResponseDTO}.
 * <p>Note: Reverse mapping from DTO to entity is currently unsupported.</p>
 */

@Service
public class NotificationResponseMapper implements DTOMapper<Notification, NotificationResponseDTO> {

    @Override
    public NotificationResponseDTO mapTo(Notification notification) {
        return new NotificationResponseDTO(
                notification.getMessage(),
                notification.getChannel(),
                notification.getStatus(),
                notification.getTimestamp()
        );
    }

    @Override
    public Notification mapFrom(NotificationResponseDTO dto) {
        throw new UnsupportedOperationException("This action is not supported!");
    }
}
