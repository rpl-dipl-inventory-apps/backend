package InventoryAPPBE.InventoryAPPBE.modelsDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductDTO extends AbstractModelDTO {
    @JsonProperty("increment_id")
    private int incrementId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("category_id")
    private int categoryId;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("price")
    private double price;

    @JsonProperty("sku")
    private String SKU;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("delete_hash")
    private String deleteHash;

    private int stock;

    @JsonProperty("stock_list")
    private List<StockDTO> stockList;
}
