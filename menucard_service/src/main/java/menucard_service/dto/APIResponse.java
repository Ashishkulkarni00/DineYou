package menucard_service.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import menucard_service.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String timestamp;
    private String requestId;
    private ErrorDetails error;


    public static <T> ResponseEntity<APIResponse<T>> success(T data, String message, String requestId, String timestamp, HttpStatus status) {
        APIResponse<T> response = APIResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(timestamp)
                .requestId(requestId)
                .error(null)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<APIResponse<T>> error(ErrorCode errorCode, String exceptionMessage, String requestId, String timestamp, HttpStatus status) {
        ErrorDetails errorDetails = errorCode.withDetails(exceptionMessage);

        APIResponse<T> response = APIResponse.<T>builder()
                .success(false)
                .message(errorCode.getMessage())
                .data(null)
                .timestamp(timestamp)
                .requestId(requestId)
                .error(errorDetails)
                .build();

        return ResponseEntity.status(status).body(response);
    }


}

