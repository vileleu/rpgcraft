package fr.jeunesauvage.entitycustom;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public interface EntityCustom {
    UUID        getUUID();
    int         getEntityId();
    String      getName();
    Location    getLocation();
    World       getWorld();
    double      getWidth();
    double      getHeight();
    boolean     isPresent();
    void        teleport(Location location);
    void        setFallDistance(Float fallDistance);
    Vector      getVelocity();
    void        setVelocity(Vector vector);
}