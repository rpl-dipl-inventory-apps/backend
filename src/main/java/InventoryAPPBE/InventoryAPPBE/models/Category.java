package InventoryAPPBE.InventoryAPPBE.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Category extends AbstractModel{
    @JsonProperty("increment_id")
    private int incrementId;

    @JsonProperty("category_name")
    private String categoryName;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
