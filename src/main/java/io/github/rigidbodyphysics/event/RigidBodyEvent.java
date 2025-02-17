package io.github.rigidbodyphysics.event;

import io.github.rigidbodyphysics.MinecraftPhysicsHandler;
import io.github.rigidbodyphysics.entity.PhysicsObject;
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
