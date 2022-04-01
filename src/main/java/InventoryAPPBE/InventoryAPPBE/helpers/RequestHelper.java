package InventoryAPPBE.InventoryAPPBE.helpers;

import InventoryAPPBE.InventoryAPPBE.models.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class RequestHelper {
    public User getUserFromContext() throws Exception {
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof AnonymousAuthenticationToken){
            throw new Exception("error instance authentication");
        }
        if(!(authentication.getPrincipal() instanceof User)){
            throw new Exception("error instance authentication principal not user");
        }
        return (User) authentication.getPrincipal();
    }
}
