package InventoryAPPBE.InventoryAPPBE.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Authentication extends AbstractModel{
    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("user_id")
    private int userId;
}
