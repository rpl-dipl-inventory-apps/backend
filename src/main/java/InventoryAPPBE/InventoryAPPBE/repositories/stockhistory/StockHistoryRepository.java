package InventoryAPPBE.InventoryAPPBE.repositories.stockhistory;

import InventoryAPPBE.InventoryAPPBE.models.StockHistory;
import InventoryAPPBE.InventoryAPPBE.models.User;

import java.util.List;

public interface StockHistoryRepository {
    public StockHistory create(StockHistory stockHistory);
    public List<StockHistory> getAll(User user);
    public Integer getTotalIn(User user);
    public Integer getTotalOut(User user);
}
