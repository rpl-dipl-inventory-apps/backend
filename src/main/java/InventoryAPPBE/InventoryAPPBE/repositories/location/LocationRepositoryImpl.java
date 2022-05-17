package InventoryAPPBE.InventoryAPPBE.repositories.location;

import InventoryAPPBE.InventoryAPPBE.models.Location;
import InventoryAPPBE.InventoryAPPBE.models.User;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class LocationRepositoryImpl implements LocationRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Location create(Location product) {
        entityManager.persist(product);
        entityManager.flush();
        return product;
    }

    @Override
    @Transactional
    public Location findById(int id, User user) {
        TypedQuery<Location> query = entityManager.createQuery("SELECT p from Location p where p.id = :id and p.user = :user", Location.class)
                .setParameter("id", id)
                .setParameter("user", user);

        List<Location> listLocation = query.getResultList();

        if (listLocation.size() == 1 && listLocation.get(0) == null || listLocation.size() == 0) {
            throw new EntityNotFoundException("location with that identification not found");
        }
        return listLocation.get(0);
    }

    @Override
    @Transactional
    public List<Location> getAllLocation(User user) {
        TypedQuery<Location> query = entityManager.createQuery("SELECT p from Location p WHERE p.user=:user", Location.class)
                .setParameter("user", user);

        return query.getResultList();
    }

    @Override
    @Transactional
    public void deleteLocation(int id) {
        Location location = entityManager.find(Location.class, id);
        entityManager.remove(location);
    }

    @Override
    @Transactional
    public Location update(Location location) {
        location = entityManager.merge(location);
        return location;
    }

    @Override
    @Transactional
    public int getLatestIncrementIdByUser(User user){
        TypedQuery<Location> query = entityManager.createQuery("SELECT p FROM Location p WHERE p.user=:user ORDER BY p.incrementId DESC", Location.class)
                .setParameter("user", user)
                .setMaxResults(1);

        List<Location> products = query.getResultList();
        if (products.size() == 1 && products.get(0) == null || products.size() == 0) {
            return 0;
        }
        return products.get(0).getIncrementId();
    }
}
