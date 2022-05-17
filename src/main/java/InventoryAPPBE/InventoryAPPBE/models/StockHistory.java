package InventoryAPPBE.InventoryAPPBE.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@DynamicUpdate
public class StockHistory extends AbstractModel{
    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_id")
    private int productId;

    @JsonProperty("location_id")
    private int locationId;

    @JsonProperty("location_name")
    private String locationName;

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("username")
    private String userName;

    @JsonProperty("stock_id")
    private int stockId;

    @JsonProperty("action_by")
    private String actionBy;

    private String type;
    private int quantity;
}
