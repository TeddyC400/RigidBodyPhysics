package io.github.tblaze.entity;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;

/**
 * An entity with no tick updates and gravity turned off.
 * This entity object is useful for working with custom physics.
 */
public class NoTickEntity extends Entity {

    public NoTickEntity(EntityType entityType) {
        super(entityType);

        this.hasPhysics = false;
        this.setNoGravity(true);
    }

    @Override
    public void tick(long time) {

    }

}
