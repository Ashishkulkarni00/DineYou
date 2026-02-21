package menucard_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchMenuItemResultsResDto {

    private Long itemId;

    private String itemName;

    private Double itemPrice;

    private String imageUrl;

    private String description;

}
