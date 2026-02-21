package payment_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitializePaymentReqDto {

    private Long amount;
    private Long quantity;
    private String productName;
    private String currency;

}
