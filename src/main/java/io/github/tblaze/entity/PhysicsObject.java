package io.github.tblaze.entity;

import com.jme3.bullet.objects.PhysicsRigidBody;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;

public interface PhysicsObject {

    void spawn(Instance instance, Point point);

    void update(float delta);

    void destroy();

    PhysicsRigidBody getRigidBody();

}
