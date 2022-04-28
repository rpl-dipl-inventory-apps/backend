package InventoryAPPBE.InventoryAPPBE.repositories.user;

import InventoryAPPBE.InventoryAPPBE.models.User;

import java.util.List;


public interface UserRepository {
    public User create(User user);
    public List<User> findById(int id);
    public User findByEmail(String email);
    public User update(User user);
    public List<User> findAll();

    public List<User> getSuppliers();
}
