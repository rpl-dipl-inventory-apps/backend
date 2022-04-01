package InventoryAPPBE.InventoryAPPBE.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@DynamicUpdate
@Table(name = "users")
public class User extends AbstractModel{
    private String username;
    private String password;

    @Column(unique=true)
    private String email;

    private String role;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("delete_hash")
    private String deleteHash;

    @OneToMany(mappedBy = "owner")
    @ToString.Exclude
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<Supplier> suppliers;

//    @OneToMany(mappedBy = "user")
//    @ToString.Exclude
//    @Getter(AccessLevel.NONE)
//    @Setter(AccessLevel.NONE)
//    private List<Product> productList;
//
//    @OneToMany(mappedBy = "user")
//    @ToString.Exclude
//    @Getter(AccessLevel.NONE)
//    @Setter(AccessLevel.NONE)
//    private List<Category> categoryList;
//
//    @OneToMany(mappedBy = "user")
//    @ToString.Exclude
//    @Getter(AccessLevel.NONE)
//    @Setter(AccessLevel.NONE)
//    private List<Location> locationList;
}
