package at.qe.skeleton.tests;

import at.qe.skeleton.dtos.NotificationResponseDTO;
import at.qe.skeleton.mappers.NotificationResponseMapper;
import at.qe.skeleton.model.Notification;
import at.qe.skeleton.model.NotificationStatus;
import at.qe.skeleton.model.NotificationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NotificationResponseMapperTest {

    @Autowired
    private NotificationResponseMapper mapper;

    @Test
    void mapTo_ShouldReturnCorrectDto() {
        String message = "Your order has been shipped";
        NotificationType channel = NotificationType.EMAIL;
        NotificationStatus status = NotificationStatus.SENT;
        LocalDateTime now = LocalDateTime.now();

        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setChannel(channel);
        notification.setStatus(status);
        notification.setTimestamp(now);

        NotificationResponseDTO result = mapper.mapTo(notification);

        assertNotNull(result);
        assertEquals(message, result.message());
        assertEquals(channel, result.channel());
        assertEquals(status, result.status());
        assertEquals(now, result.timestamp());
    }

    @Test
    void mapFrom_ShouldThrowUnsupportedOperationException() {
        NotificationResponseDTO dto = new NotificationResponseDTO(
                "Test",
                NotificationType.EMAIL,
                NotificationStatus.QUEUED,
                LocalDateTime.now()
        );

        assertThrows(UnsupportedOperationException.class, () ->
                mapper.mapFrom(dto)
        );
    }
}