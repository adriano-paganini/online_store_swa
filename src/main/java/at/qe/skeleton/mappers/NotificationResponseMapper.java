package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.NotificationResponseDTO;
import at.qe.skeleton.model.Notification;
import org.springframework.stereotype.Service;

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
