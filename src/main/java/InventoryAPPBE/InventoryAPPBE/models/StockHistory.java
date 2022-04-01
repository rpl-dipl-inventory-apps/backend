package InventoryAPPBE.InventoryAPPBE.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;

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

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("stock_id")
    private int stockId;

    @JsonProperty("action_by")
    private String actionBy;

    private String type;
    private int quantity;
}
