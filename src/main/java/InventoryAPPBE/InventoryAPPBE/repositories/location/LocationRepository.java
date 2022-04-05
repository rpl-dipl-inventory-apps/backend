package InventoryAPPBE.InventoryAPPBE.repositories.location;

import InventoryAPPBE.InventoryAPPBE.models.Location;
import InventoryAPPBE.InventoryAPPBE.models.User;

import java.util.List;

public interface LocationRepository {
    public Location create(Location location);
    public Location findById(int id, User user);
    public List<Location> getAllLocation(User user);
    public void deleteLocation(int id);
    public Location update(Location location);
    public int getLatestIncrementIdByUser(User user);
}
