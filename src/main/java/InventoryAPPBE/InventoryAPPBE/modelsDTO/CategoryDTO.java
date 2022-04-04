package InventoryAPPBE.InventoryAPPBE.modelsDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CategoryDTO extends AbstractModelDTO{
    @JsonProperty("increment_id")
    private int incrementId;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("user_id")
    private int userId;
}
