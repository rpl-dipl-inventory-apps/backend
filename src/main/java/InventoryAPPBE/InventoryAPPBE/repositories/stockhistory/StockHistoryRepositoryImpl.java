package InventoryAPPBE.InventoryAPPBE.repositories.stockhistory;

import InventoryAPPBE.InventoryAPPBE.models.StockHistory;
import InventoryAPPBE.InventoryAPPBE.models.User;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Component
public class StockHistoryRepositoryImpl implements StockHistoryRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public StockHistory create(StockHistory stockHistory) {
        entityManager.persist(stockHistory);
        entityManager.flush();
        return stockHistory;
    }

    @Override
    public List<StockHistory> getAll(User user) {
        TypedQuery<StockHistory> query = entityManager.createQuery("SELECT s FROM StockHistory s WHERE s.userId = :user", StockHistory.class)
                .setParameter("user", user.getId());
        return query.getResultList();
    }

    public Integer getTotalIn(User user) {
        TypedQuery<Long> query = entityManager.createQuery("SELECT SUM(s.quantity) FROM StockHistory s WHERE s.userId = :user AND s.type = 'in'", Long.class)
                .setParameter("user", user.getId());
        List<Long> result = query.getResultList();
        if (result.size() == 1 && result.get(0) == null || result.size() == 0) {
            return 0;
        }
        return Math.toIntExact(result.get(0));
    }

    public Integer getTotalOut(User user) {
        TypedQuery<Long> query = entityManager.createQuery("SELECT SUM(s.quantity) FROM StockHistory s WHERE s.userId = :user AND s.type = 'out'", Long.class)
                .setParameter("user", user.getId());
        List<Long> result = query.getResultList();
        if (result.size() == 1 && result.get(0) == null || result.size() == 0) {
            return 0;
        }
        return Math.toIntExact(result.get(0));
    }
}
