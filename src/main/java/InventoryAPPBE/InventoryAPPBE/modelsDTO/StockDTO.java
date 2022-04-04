package InventoryAPPBE.InventoryAPPBE.modelsDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder(toBuilder=true)
@Data
public class StockDTO extends AbstractModelDTO {
    @JsonProperty("location_id")
    private int locationId;

    @JsonProperty("location_name")
    private String locationName;

    @JsonProperty("quantity")
    private int quantity;
}
