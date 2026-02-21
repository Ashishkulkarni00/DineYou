package menucard_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Menucard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menucardId;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @OneToMany(mappedBy = "menucard", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ItemCategory> categories = new ArrayList<>();

    private String createdAt;

    private String updatedAt;

}
