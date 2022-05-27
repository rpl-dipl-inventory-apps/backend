package InventoryAPPBE.InventoryAPPBE.repositories.supplier;

import InventoryAPPBE.InventoryAPPBE.models.Supplier;
import InventoryAPPBE.InventoryAPPBE.models.User;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Component
public class SupplierRepositoryImpl implements SupplierRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Supplier addSupplier(Supplier supplier){
        entityManager.persist(supplier);
        return supplier;
    }

    @Override
    @Transactional
    public Supplier getById(int id){
        return entityManager.find(Supplier.class, id);
    }

    @Override
    @Transactional
    public void deleteSupplier(Supplier supplier){
        Supplier supplierToDelete = entityManager.find(Supplier.class, supplier.getId());
        entityManager.remove(supplierToDelete);
    }

    @Override
    @Transactional
    public User verifySupplier(int ownerId, int supplierId){
        TypedQuery<Supplier> query = entityManager.createQuery("SELECT s FROM Supplier s WHERE s.owner.id = :ownerId AND s.supplier.id = :supplierId", Supplier.class)
                .setParameter("ownerId", ownerId)
                .setParameter("supplierId", supplierId)
                .setMaxResults(1);
        List<Supplier> suppliers = query.getResultList();
        if (suppliers.size() == 1 && suppliers.get(0) == null || suppliers.size() == 0){
            throw new EntityNotFoundException("identification not found");
        }

        return suppliers.get(0).getOwner();
    }

    public List<Supplier> getAllByOwner(User owner){
        TypedQuery<Supplier> query = entityManager.createQuery("SELECT s FROM Supplier s WHERE s.owner.id = :ownerId", Supplier.class)
                .setParameter("ownerId", owner.getId());
        return query.getResultList();
    }

    public List<Supplier> getAllBySupplier(User supplier){
        TypedQuery<Supplier> query = entityManager.createQuery("SELECT s FROM Supplier s WHERE s.supplier.id = :supplierId", Supplier.class)
                .setParameter("supplierId", supplier.getId());
        return query.getResultList();
    }

    public List<Integer> getListOfSuppliers(int ownerId){
        TypedQuery<Integer> query = entityManager.createQuery("SELECT s.supplier.id FROM Supplier s WHERE s.owner.id = :ownerId", Integer.class)
                .setParameter("ownerId", ownerId);
        return query.getResultList();
    }
}
