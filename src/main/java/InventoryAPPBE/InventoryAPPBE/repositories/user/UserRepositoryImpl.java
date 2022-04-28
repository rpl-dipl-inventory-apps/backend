package InventoryAPPBE.InventoryAPPBE.repositories.user;

import InventoryAPPBE.InventoryAPPBE.models.User;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserRepositoryImpl implements UserRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public User create(User user) {
        entityManager.persist(user);
        entityManager.flush();

        return user;
    }

    @Override
    @Transactional
    public List<User> findById(int id) {
        User userFound = entityManager.find(User.class, id);
        List<User> listUser = new ArrayList<>();
        listUser.add(userFound);
        return listUser;
    }

    @Override
    @Transactional
    public User findByEmail(String email){
        return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
    }
    
    @Override
    @Transactional
    public User update(User user){
        user = entityManager.merge(user);
        
        return user;
    }

    @Override
    @Transactional
    public List<User> findAll(){
        return entityManager.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
    }

    @Override
    @Transactional
    public List<User> getSuppliers(){
        return entityManager.createQuery("SELECT u FROM User u WHERE u.role = 'supplier'", User.class)
                .getResultList();
    }
}
