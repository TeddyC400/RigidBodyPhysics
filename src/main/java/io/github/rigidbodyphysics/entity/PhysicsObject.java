package io.github.rigidbodyphysics.entity;

import com.jme3.bullet.objects.PhysicsBody;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;

/**
 * An object with physics attached to it.
 */
public interface PhysicsObject<T extends PhysicsBody> {

    /**
     * Spawns the object at the specified point.
     *
     * @param instance
     * @param point
     */
    void spawn(Instance instance, Point point);

    /**
     * Updates the object continuously on the tick scheduler from {@link io.github.rigidbodyphysics.MinecraftPhysicsHandler}.
     *
     * @param delta
     */
    void update(float delta);

    /**
     * Removes the object completely.
     */
    void destroy();

    /**
     * @return PhysicsBody object
     */
    T getPhysicsBody();

}
