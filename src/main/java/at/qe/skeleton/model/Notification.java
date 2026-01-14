package at.qe.skeleton.model;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Notification implements Persistable<Long>, Serializable, Comparable<Notification> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 5000)
    String message;

    @Column(nullable = false)
    NotificationType channel;

    @Column(nullable = false)
    NotificationStatus status;

    @CreationTimestamp
    LocalDateTime timestamp;

    public Notification(Long userId, String message, NotificationType channel) {
        this.userId = userId;
        this.message = message;
        this.channel = channel;
        this.status = NotificationStatus.QUEUED;
    }

    public Notification() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setChannel(NotificationType channel) {
        this.channel = channel;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public NotificationType getChannel() {
        return channel;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(Notification n) {
        assert n.getId() != null;
        return this.id.compareTo(n.getId());
    }

    @Nullable
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return (null == id);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.getId());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Notification other)) {
            return false;
        }
        return Objects.equals(this.getId(), other.getId());
    }

    @Override
    public String toString() {
        return "at.qe.skeleton.model.Notification[ id=" + id + " ]";
    }


}
