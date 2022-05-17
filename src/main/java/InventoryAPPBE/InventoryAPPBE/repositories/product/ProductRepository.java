package InventoryAPPBE.InventoryAPPBE.repositories.product;

import InventoryAPPBE.InventoryAPPBE.models.Product;
import InventoryAPPBE.InventoryAPPBE.models.Stock;
import InventoryAPPBE.InventoryAPPBE.models.User;

import java.util.ArrayList;
import java.util.List;

public interface ProductRepository {
    public Product create(Product product);
    public Product findById(int id, User user);
    public List<Product> getRecentProducts(User user);
    public List<Product> getAllProduct(User user);
    public void deleteProduct(int id);
    public Product update(Product product);
    public Integer getTotalProduct(User user);
    public int getLatestIncrementIdByUser(User user);
    public List<Stock> getRequestedStock(int productId, int qtyRequested);
    public void deleteStock(Stock stock);
}
