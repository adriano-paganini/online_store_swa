package at.qe.skeleton.tests;

import at.qe.skeleton.model.Subscription;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubscriptionTest {

    @Test
    public void testToString(){
        Subscription s = new Subscription();
        s.setId(42L);

        assertEquals("at.qe.skeleton.model.Subscription[ id=42 ]", s.toString());

        Subscription s2 = new Subscription();
        s2.setId(null);
        assertEquals("at.qe.skeleton.model.Subscription[ id=null ]", s2.toString());
    }

    @Test
    public void testEquals(){
        Subscription a = new Subscription();
        Subscription b = new Subscription();

        assertNotEquals( a,null);

        assertNotEquals(a,"not a subscription");

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
    public void testHashCode(){
        Subscription s1 = new Subscription();
        Subscription s2 = new Subscription();

        s1.setId(null);
        s2.setId(null);
        assertEquals(s1.hashCode(), s2.hashCode());

        s1.setId(10L);
        s2.setId(10L);
        assertEquals(s1.hashCode(), s2.hashCode());

        s2.setId(11L);
        assertNotEquals(s1.hashCode(), s2.hashCode());
    }
}
