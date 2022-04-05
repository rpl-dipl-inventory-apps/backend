package InventoryAPPBE.InventoryAPPBE.repositories.supplier;

import InventoryAPPBE.InventoryAPPBE.models.Supplier;
import InventoryAPPBE.InventoryAPPBE.models.User;

public interface SupplierRepository {
    public Supplier addSupplier(Supplier supplier);
    public Supplier getById(int id);
    public void deleteSupplier(Supplier supplier);
    public User verifySupplier(int ownerId, int supplierId);
}
