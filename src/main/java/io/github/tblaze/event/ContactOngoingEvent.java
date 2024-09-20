package io.github.tblaze.event;

import io.github.tblaze.MinecraftPhysicsHandler;
import io.github.tblaze.entity.PhysicsObject;
import net.minestom.server.event.trait.CancellableEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the rigid body is still in contact with the other rigid body.
 * This will be called repeatedly  until there is no more contact between the two objects.
 */
public class ContactOngoingEvent implements RigidBodyEvent, CancellableEvent {

    private final MinecraftPhysicsHandler physicsHandler;
    private final PhysicsObject physicsObjectA;
    private final PhysicsObject physicsObjectB;

    private boolean cancelled;

    public ContactOngoingEvent(@NotNull MinecraftPhysicsHandler physicsHandler,
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
