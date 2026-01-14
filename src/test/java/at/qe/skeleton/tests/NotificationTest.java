package at.qe.skeleton.tests;

import at.qe.skeleton.model.Notification;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationTest {

    @Test
    public void testHashCode(){
        Notification n1 = new Notification();
        Notification n2 = new Notification();

        n1.setId(null);
        n2.setId(null);
        assertEquals(n1.hashCode(), n2.hashCode());

        n1.setId(5L);
        n2.setId(5L);
        assertEquals(n1.hashCode(), n2.hashCode());

        n2.setId(6L);
        assertNotEquals(n1.hashCode(), n2.hashCode());
    }

    @Test
    public void testEquals(){
        Notification a = new Notification();
        Notification b = new Notification();

        assertNotEquals(a, null);

        assertNotEquals(a, "not a notification");

        a.setId(null);
        b.setId(null);
        assertEquals(a, b);

        a.setId(1L);
        b.setId(1L);
        assertEquals(a, b);

        b.setId(2L);
        assertNotEquals(a, b);

        a.setId(null);
        b.setId(2L);
        assertNotEquals(a, b);

        a.setId(2L);
        b.setId(null);
        assertNotEquals(a, b);
    }

    @Test
    public void testToString(){
        Notification n = new Notification();
        n.setId(10L);

        assertEquals(
                "at.qe.skeleton.model.Notification[ id=10 ]",
                n.toString()
        );

        n.setId(null);
        assertEquals(
                "at.qe.skeleton.model.Notification[ id=null ]",
                n.toString()
        );
    }

    @Test
    public void testCompareTo(){
        Notification n1 = new Notification();
        Notification n2 = new Notification();

        n1.setId(1L);
        n2.setId(2L);

        assertTrue(n1.compareTo(n2) < 0);
        assertTrue(n2.compareTo(n1) > 0);

        n2.setId(1L);
        assertEquals(0, n1.compareTo(n2));

        Notification n3 = new Notification();
        n3.setId(null);

        AssertionError error = assertThrows(
                AssertionError.class,
                () -> n1.compareTo(n3)
        );
        assertNotNull(error);
    }
    @Test
    public void testGetTimestamp(){
        Notification notification = new Notification();
        LocalDateTime timestamp = LocalDateTime.now();

        notification.setTimestamp(timestamp);

        assertEquals(timestamp, notification.getTimestamp());
    }

}
