package InventoryAPPBE.InventoryAPPBE.repositories.category;

import InventoryAPPBE.InventoryAPPBE.models.Category;
import InventoryAPPBE.InventoryAPPBE.models.User;

import java.util.List;

public interface CategoryRepository {
    public Category create(Category category);
    public Category findById(int id, User user);
    public List<Category> getAllCategory(User user);
    public void deleteCategory(int id);
    public Category update(Category category);
    public int getLatestIncrementIdByUser(User user);
}
