package InventoryAPPBE.InventoryAPPBE.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder(toBuilder=true)
@Entity
@DynamicUpdate
public class Product extends AbstractModel {
    @JsonProperty("increment_id")
    private int incrementId;

    @JsonProperty("product_name")
    @Column
    private String productName;

    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;

    @JsonProperty("price")
    private double price;

    @JsonProperty("sku")
    private String SKU;

    private int stock;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("delete_hash")
    private String deleteHash;

    @JsonProperty("stock_list")
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true)
    @JoinColumn(name = "product_id")
    @ToString.Exclude
    private List<Stock> stockList;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
