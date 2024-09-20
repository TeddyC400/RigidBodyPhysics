package io.github.tblaze.entity;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;

/**
 * Attaches an entity with physics.
 * This is primarily only used for the item display entity.
 * <br><br>
 * Block display entity does not work that well due to the origin
 * point being at the corner instead of the center, causing
 * the physics to look visually unappealing.
 */
public interface PhysicsEntityObject extends PhysicsObject {

    /**
     * @return Entity object
     */
    Entity getEntity();

    /**
     * @return ItemDisplayMeta object
     */
    ItemDisplayMeta getMeta();

}
