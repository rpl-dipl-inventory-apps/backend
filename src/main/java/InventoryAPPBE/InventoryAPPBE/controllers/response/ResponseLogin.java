package InventoryAPPBE.InventoryAPPBE.controllers.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseLogin {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
