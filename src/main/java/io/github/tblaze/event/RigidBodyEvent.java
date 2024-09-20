package io.github.tblaze.event;

import io.github.tblaze.MinecraftPhysicsHandler;
import io.github.tblaze.entity.PhysicsObject;
import net.minestom.server.event.Event;
import org.jetbrains.annotations.NotNull;

public interface RigidBodyEvent extends Event {

    /**
     * Gets the physics handler
     *
     * @return the physics handler
     */
    @NotNull MinecraftPhysicsHandler getPhysicsHandler();

    /**
     * Gets the physics object
     *
     * @return the physics object
     */
    @NotNull PhysicsObject getPhysicsObject();

}
