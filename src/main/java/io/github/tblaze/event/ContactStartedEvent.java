package io.github.tblaze.event;

import io.github.tblaze.MinecraftPhysicsHandler;
import io.github.tblaze.entity.PhysicsObject;
import net.minestom.server.event.trait.CancellableEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a rigid body makes first contact with another rigid body.
 */
public class ContactStartedEvent implements RigidBodyEvent, CancellableEvent {

    private final MinecraftPhysicsHandler physicsHandler;
    private final PhysicsObject physicsObjectA;
    private final PhysicsObject physicsObjectB;

    private boolean cancelled;

    public ContactStartedEvent(@NotNull MinecraftPhysicsHandler physicsHandler,
                               @NotNull PhysicsObject physicsObjectA, @NotNull PhysicsObject physicsObjectB) {
        this.physicsHandler = physicsHandler;
        this.physicsObjectA = physicsObjectA;
        this.physicsObjectB = physicsObjectB;
    }

    @Override
    public @NotNull MinecraftPhysicsHandler getPhysicsHandler() {
        return physicsHandler;
    }

    @Override
    public @NotNull PhysicsObject getPhysicsObject() {
        return physicsObjectA;
    }

    public @NotNull PhysicsObject getSecondPhysicsObject() {
        return physicsObjectB;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
