package InventoryAPPBE.InventoryAPPBE.repositories.authentication;

import InventoryAPPBE.InventoryAPPBE.models.Authentication;

public interface AuthenticationRepository {
    public Authentication create(Authentication authentication);
    public Authentication getByRefreshToken(String token);
    public void deleteByUserId(int userId);
}
