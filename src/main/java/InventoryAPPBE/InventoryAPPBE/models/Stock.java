package InventoryAPPBE.InventoryAPPBE.models;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder(toBuilder=true)
@Entity
@DynamicUpdate
public class Stock extends AbstractModel{
    @ManyToOne
    @JoinColumn(name="location_id")
    private Location location;

    private int quantity;
}
