package order_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "request_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Service Info
    @Column(nullable = false)
    private String serviceName;          // restaurant-service, order-service, etc.

    // 🔹 Request Info
    private String requestId;            // UUID shared across all services
    private String spanId;               // optional for distributed tracing
    private String httpMethod;           // GET, POST, etc.
    private String requestPath;          // /api/v1/cart/add
    @Column(columnDefinition = "TEXT")
    private String queryParams;          // a=b&c=d
    @Column(columnDefinition = "TEXT")
    private String requestBody;          // JSON request body

    // 🔹 Response Info
    private Integer statusCode;          // 200, 400, 500
    private Boolean success;             // true if status < 400
    private Long durationMs;             // time taken by API
    private String requestTimestamp;     // when request started
    private String responseTimestamp;    // when response completed

    // 🔹 Auth Info
    private String userId;               // Keycloak "sub"
    private String keycloakSessionId;    // Keycloak "sid"
    private String anonymousSessionId;   // Browser session id
    private String s2sClientId;          // Service-to-service client id

    // 🔹 Client & Device Info (from headers)
    private String deviceType;           // mobile/desktop/tablet
    private String userAgent;            // full agent string
    private String platform;             // iOS/Android/Web
    private String ipAddress;            // real client IP
    private String appVersion;           // UI version
    private String timezone;             // Asia/Kolkata, etc.

    // 🔹 Optional Behavioral Metadata
    private String interactionType;      // API_CALL or UI_ACTION

    // 🔹 Error Details (only when exception occurs)
    private String errorCode;
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    @Column(columnDefinition = "TEXT")
    private String errorStackTrace;

    // 🔹 Timestamp
    private String timestamp;            // ISO date-time when log saved


}
