package InventoryAPPBE.InventoryAPPBE.repositories.category;

import InventoryAPPBE.InventoryAPPBE.models.Category;
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
public class CategoryRepositoryImpl implements CategoryRepository{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Category create(Category category) {
        entityManager.persist(category);
        entityManager.flush();
        return category;
    }

    @Override
    @Transactional
    public Category findById(int id, User user) {
        TypedQuery<Category> query = entityManager.createQuery("SELECT c from Category c where c.id = :id and c.user = :user", Category.class)
                .setParameter("id", id)
                .setParameter("user", user);
        List<Category> listCategory = query.getResultList();

        if (listCategory.size() == 1 && listCategory.get(0) == null || listCategory.size() == 0) {
            throw new EntityNotFoundException("category with that identification not found");
        }
        return listCategory.get(0);
    }

    @Override
    @Transactional
    public List<Category> getAllCategory(User user) {
        TypedQuery<Category> query = entityManager.createQuery("SELECT c from Category c WHERE c.user=:user", Category.class)
                .setParameter("user", user);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void deleteCategory(int id) {
        Category category = entityManager.find(Category.class, id);
        entityManager.remove(category);
    }

    @Override
    @Transactional
    public Category update(Category category) {
        category = entityManager.merge(category);
        return category;
    }

    @Override
    @Transactional
    public int getLatestIncrementIdByUser(User user){
        TypedQuery<Category> query = entityManager.createQuery("SELECT p FROM Category p WHERE p.user=:user ORDER BY p.incrementId DESC", Category.class)
                .setParameter("user", user)
                .setMaxResults(1);

        List<Category> products = query.getResultList();
        if (products.size() == 1 && products.get(0) == null || products.size() == 0) {
            return 0;
        }
        return products.get(0).getIncrementId();
    }
}
