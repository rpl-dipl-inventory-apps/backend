package InventoryAPPBE.InventoryAPPBE.modelsDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SupplierDTO extends AbstractModelDTO {
    @JsonProperty("owner_id")
    private int ownerId;

    @JsonProperty("supplier_id")
    private int supplierId;

    @JsonProperty("supplier_email")
    private String supplierEmail;
}
