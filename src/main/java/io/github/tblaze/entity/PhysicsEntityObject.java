package io.github.tblaze.entity;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;

public interface PhysicsEntityObject extends PhysicsObject {

    Entity getEntity();

    ItemDisplayMeta getMeta();

}
