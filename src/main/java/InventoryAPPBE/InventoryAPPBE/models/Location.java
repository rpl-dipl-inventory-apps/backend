package InventoryAPPBE.InventoryAPPBE.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

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
@DynamicUpdate
public class Location extends AbstractModel {
    @JsonProperty("increment_id")
    private int incrementId;

    @JsonProperty("location_name")
    private String locationName;

    @JsonProperty("location_code")
    private String locationCode;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
