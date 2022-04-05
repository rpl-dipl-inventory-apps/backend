package InventoryAPPBE.InventoryAPPBE.repositories.product;


import InventoryAPPBE.InventoryAPPBE.models.Product;
import InventoryAPPBE.InventoryAPPBE.models.Stock;
import InventoryAPPBE.InventoryAPPBE.models.Supplier;
import InventoryAPPBE.InventoryAPPBE.models.User;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductRepositoryImpl implements ProductRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Product create(Product product) {
        entityManager.persist(product);
        entityManager.flush();
        return product;
    }

    @Override
    public Product findById(int id, User user) {
        TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p  WHERE p.id=:id AND p.user=:user", Product.class)
                .setParameter("id", id)
                .setParameter("user", user);
        List<Product> products = query.getResultList();
        if (products.size() == 1 && products.get(0) == null || products.size() == 0) {
            throw new EntityNotFoundException("product with that identification not found");
        }

        return products.get(0);
    }

    @Transactional
    public Integer getTotalProduct(User user) {
        TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p WHERE p.user=:user", Product.class)
                .setParameter("user", user);

        return query.getResultList().size();
    }

    @Override
    @Transactional
    public List<Product> getRecentProducts(User user) {
        TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p WHERE p.user=:user ORDER BY p.createdAt DESC", Product.class)
                .setParameter("user", user)
                .setMaxResults(4);

        return query.getResultList();
    }

    @Override
    @Transactional
    public List<Product> getAllProduct(User user) {
        TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p WHERE p.user=:user", Product.class)
                .setParameter("user", user);

        return query.getResultList();
    }

    @Override
    @Transactional
    public void deleteProduct(int id) {
        Product product = entityManager.find(Product.class, id);
        entityManager.remove(product);
    }

    @Override
    @Transactional
    public Product update(Product product) {
        product = entityManager.merge(product);
        return product;
    }

    @Override
    @Transactional
    public int getLatestIncrementIdByUser(User user){
        TypedQuery<Product> query = entityManager.createQuery("SELECT p FROM Product p WHERE p.user=:user ORDER BY p.incrementId DESC", Product.class)
                .setParameter("user", user)
                .setMaxResults(1);

        List<Product> products = query.getResultList();
        if (products.size() == 1 && products.get(0) == null || products.size() == 0) {
            return 0;
        }
        return products.get(0).getIncrementId();
    }

    @Override
    @Transactional
    public List<Stock> getRequestedStock(int productId, int qtyRequested) {
//      SELECT * FROM (SELECT *, SUM(quantity) OVER (ORDER BY created_at ASC)- quantity AS qty_sum FROM (SELECT * FROM stock WHERE product_id = 2)y) x WHERE qty_sum <= 30 AND quantity != 0
        Query query = entityManager.createNativeQuery("SELECT id FROM (SELECT *, SUM(quantity) OVER (ORDER BY id ASC)- quantity AS qty_sum FROM (SELECT * FROM stock WHERE product_id = :productId)y) x WHERE qty_sum < :qtyRequested  AND quantity != 0");
        query.setParameter("productId", productId);
        query.setParameter("qtyRequested", qtyRequested);
        List<Integer> stockIds = query.getResultList();


        List<Stock> stocks = new ArrayList<>();
        for (Integer stockId : stockIds) {
            stocks.add(entityManager.find(Stock.class, stockId));
        }

        System.out.println(qtyRequested);
        System.out.println(stockIds);
        return stocks;
    }

    @Override
    @Transactional
    public void deleteStock(Stock stock){
        Stock stockDeleted = entityManager.find(Stock.class, stock.getId());
        entityManager.remove(stockDeleted);
    }
}


