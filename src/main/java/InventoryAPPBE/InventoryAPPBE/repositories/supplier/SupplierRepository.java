package InventoryAPPBE.InventoryAPPBE.repositories.supplier;

import InventoryAPPBE.InventoryAPPBE.models.Supplier;
import InventoryAPPBE.InventoryAPPBE.models.User;

import java.util.List;

public interface SupplierRepository {
    public Supplier addSupplier(Supplier supplier);
    public Supplier getById(int id);
    public void deleteSupplier(Supplier supplier);
    public User verifySupplier(int ownerId, int supplierId);

    public List<Integer> getListOfSuppliers(int ownerId);
    public List<Supplier> getAllByOwner(User owner);

    public List<Supplier> getAllBySupplier(User supplier);
}
