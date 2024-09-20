package io.github.tblaze.entity;

import com.jme3.bullet.objects.PhysicsRigidBody;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;

/**
 * An object with physics attached to it.
 */
public interface PhysicsObject {

    /**
     * Spawns the object at the specified point.
     *
     * @param instance
     * @param point
     */
    void spawn(Instance instance, Point point);

    /**
     * Updates the object continuously on the tick scheduler from {@link io.github.tblaze.MinecraftPhysicsHandler}.
     *
     * @param delta
     */
    void update(float delta);

    /**
     * Removes the object completely.
     */
    void destroy();

    /**
     * @return PhysicsRigidBody object
     */
    PhysicsRigidBody getRigidBody();

}
