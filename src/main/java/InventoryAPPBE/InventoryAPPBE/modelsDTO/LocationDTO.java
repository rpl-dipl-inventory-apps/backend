package InventoryAPPBE.InventoryAPPBE.modelsDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LocationDTO extends AbstractModelDTO{
    @JsonProperty("increment_id")
    private int incrementId;

    @JsonProperty("location_name")
    private String locationName;

    @JsonProperty("location_code")
    private String locationCode;

    @JsonProperty("user_id")
    private int userId;
}
