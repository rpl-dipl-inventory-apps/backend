package InventoryAPPBE.InventoryAPPBE.repositories.authentication;

import InventoryAPPBE.InventoryAPPBE.models.Authentication;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Component
public class AuthenticationRepositoryImpl implements AuthenticationRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Authentication create(Authentication authentication) {
        entityManager.persist(authentication);
        entityManager.flush();

        return authentication;
    }

    @Override
    @Transactional
    public Authentication getByRefreshToken(String token) {
        return entityManager.createQuery("SELECT a FROM Authentication a WHERE a.refreshToken = :refreshToken", Authentication.class)
                .setParameter("refreshToken", token)
                .getSingleResult();
    }

    @Override
    @Transactional
    public void deleteByUserId(int userId) {
        Authentication auth = entityManager.createQuery("SELECT a FROM Authentication a WHERE a.userId = :userId", Authentication.class)
                .setParameter("userId", userId)
                .getSingleResult();
        entityManager.remove(auth);
    }
}
