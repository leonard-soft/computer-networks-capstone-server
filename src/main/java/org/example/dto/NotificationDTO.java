package org.example.dto;

import java.io.Serializable;

/**
 * A generic Data Transfer Object for sending real-time notifications from the server to clients.
 * It consists of a type to identify the notification's purpose and a payload with relevant data.
 */
public class NotificationDTO implements Serializable {
    private final String notificationType;
    private final Object payload;

    /**
     * Constructs a new NotificationDTO.
     *
     * @param notificationType A string identifier for the type of notification (e.g., "GAME_INVITATION").
     * @param payload          An object containing the data for the notification.
     */
    public NotificationDTO(String notificationType, Object payload) {
        this.notificationType = notificationType;
        this.payload = payload;
    }

    /**
     * Gets the type of the notification.
     *
     * @return A string representing the notification's type.
     */
    public String getNotificationType() {
        return notificationType;
    }

    /**
     * Gets the payload of the notification.
     *
     * @return An object containing the notification's data.
     */
    public Object getPayload() {
        return payload;
    }
}
