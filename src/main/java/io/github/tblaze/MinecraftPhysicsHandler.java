package io.github.tblaze;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PersistentManifolds;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import io.github.tblaze.entity.PhysicsObject;
import io.github.tblaze.event.ContactEndedEvent;
import io.github.tblaze.event.ContactOngoingEvent;
import io.github.tblaze.event.ContactStartedEvent;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The main logic for updating the physics continuously.
 */
public class MinecraftPhysicsHandler {

    private static int UPDATABLE_TICK = 1;

    private PhysicsSpace physicsSpace;
    private boolean paused;

    // cached
    private long lastRan;
    private long diff;
    private float deltaTime;

    private final List<PhysicsObject> PHYSICS_OBJECTS = new CopyOnWriteArrayList<>();
    private final Map<Point, PhysicsObject> POINT_MAP = new HashMap<>();
    private final Map<Entity, PhysicsObject> ENTITY_MAP = new HashMap<>();
    private final Map<PhysicsCollisionObject, PhysicsObject> COLLISION_MAP = new HashMap<>();
    private final Instance instance;

    public MinecraftPhysicsHandler(Instance instance, boolean listenToContactEnded, boolean listenToContactOngoing, boolean listenToContactStarted) {
        this.instance = instance;
        this.paused = false;
        this.lastRan = System.nanoTime();

        MinecraftPhysicsHandler physicsHandler = this;
        this.physicsSpace = new PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT) {
            // https://stephengold.github.io/Libbulletjme/lbj-en/English/collision.html#_custom_contact_handling
            // Must override to properly listen to the contact listeners

            @Override
            public void update(float timeInterval, int maxSteps) {
                update(timeInterval, maxSteps, listenToContactEnded, listenToContactOngoing, listenToContactStarted);
            }

            @Override
            public void onContactEnded(long manifoldId) {
                long idBodyA = PersistentManifolds.getBodyAId(manifoldId);
                long idBodyB = PersistentManifolds.getBodyBId(manifoldId);
                PhysicsCollisionObject objA = PhysicsCollisionObject.findInstance(idBodyA);
                PhysicsCollisionObject objB = PhysicsCollisionObject.findInstance(idBodyB);
                if (COLLISION_MAP.containsKey(objA) && COLLISION_MAP.containsKey(objB)) {
                    PhysicsObject physicsObjectA = COLLISION_MAP.get(objA);
                    PhysicsObject physicsObjectB = COLLISION_MAP.get(objB);

                    ContactEndedEvent event = new ContactEndedEvent(physicsHandler, physicsObjectA, physicsObjectB);
                    EventDispatcher.call(event);
                }
            }

            @Override
            public void onContactProcessed(PhysicsCollisionObject pcoA, PhysicsCollisionObject pcoB, long pointId) {
                if (COLLISION_MAP.containsKey(pcoA) && COLLISION_MAP.containsKey(pcoB)) {
                    PhysicsObject physicsObjectA = COLLISION_MAP.get(pcoA);
                    PhysicsObject physicsObjectB = COLLISION_MAP.get(pcoB);

                    ContactOngoingEvent event = new ContactOngoingEvent(physicsHandler, physicsObjectA, physicsObjectB);
                    EventDispatcher.call(event);
                }
            }

            @Override
            public void onContactStarted(long manifoldId) {
                long idBodyA = PersistentManifolds.getBodyAId(manifoldId);
                long idBodyB = PersistentManifolds.getBodyBId(manifoldId);
                PhysicsCollisionObject objA = PhysicsCollisionObject.findInstance(idBodyA);
                PhysicsCollisionObject objB = PhysicsCollisionObject.findInstance(idBodyB);
                if (COLLISION_MAP.containsKey(objA) && COLLISION_MAP.containsKey(objB)) {
                    PhysicsObject physicsObjectA = COLLISION_MAP.get(objA);
                    PhysicsObject physicsObjectB = COLLISION_MAP.get(objB);

                    ContactStartedEvent event = new ContactStartedEvent(physicsHandler, physicsObjectA, physicsObjectB);
                    EventDispatcher.call(event);
                }
            }
        };

        instance.scheduler().buildTask(() -> {
            this.diff = System.nanoTime() - this.lastRan;
            this.deltaTime = this.diff / 1_000_000_000f;

            this.lastRan = System.nanoTime();
            if (!this.paused) {
                update(this.deltaTime);
            }
        }).repeat(TaskSchedule.tick(UPDATABLE_TICK)).schedule();
    }

    private void update(float delta) {
        if (physicsSpace == null)
            return;

        physicsSpace.update(delta);

        for (var object : PHYSICS_OBJECTS) {
            object.update(delta);
        }
    }

    /**
     * Checks if {@link PhysicsCollisionObject} is also a {@link PhysicsObject} in Minecraft.
     *
     * @param object
     * @return True if the PhysicsCollisionObject is a PhysicsObject
     */
    public boolean hasMinecraftPhysicsObject(PhysicsCollisionObject object) {
        return COLLISION_MAP.containsKey(object);
    }

    /**
     * Gets the {@link PhysicsObject} placed at the specific point.
     * Note that this is primarily used for static blocks that do not dynamically move.
     *
     * @param point
     * @return True if there is a PhysicsObject at the given location
     */
    public PhysicsObject getFromPoint(Point point) {
        return POINT_MAP.get(point);
    }

    /**
     * Gets the {@link PhysicsObject} if it is attached to the {@link Entity}.
     *
     * @param entity
     * @return True if the PhysicsObject is linked to the Entity
     */
    public PhysicsObject getFromEntity(Entity entity) {
        return ENTITY_MAP.get(entity);
    }

    /**
     * Adds the {@link PhysicsObject} to the specified {@link Point} in the world.
     *
     * @param point
     * @param physicsObject
     */
    public void addToInstance(Point point, PhysicsObject physicsObject) {
        POINT_MAP.put(point, physicsObject);
        COLLISION_MAP.put(physicsObject.getRigidBody(), physicsObject);
    }

    /**
     * Attaches the {@link PhysicsObject} to the {@link Entity}.
     *
     * @param entity
     * @param physicsObject
     */
    public void addToInstance(Entity entity, PhysicsObject physicsObject) {
        ENTITY_MAP.put(entity, physicsObject);
        COLLISION_MAP.put(physicsObject.getRigidBody(), physicsObject);
    }

    /**
     * Removes the {@link PhysicsObject} from the specified {@link Point} in the world.
     *
     * @param point
     */
    public void removeFromInstance(Point point) {
        if (POINT_MAP.containsKey(point)) {
            PhysicsObject physicsObject = POINT_MAP.get(point);
            POINT_MAP.remove(point);
            COLLISION_MAP.remove(physicsObject.getRigidBody());
        }
    }

    /**
     * Removes the {@link PhysicsObject} from the {@link Entity}.
     *
     * @param entity
     */
    public void removeFromInstance(Entity entity) {
        if (ENTITY_MAP.containsKey(entity)) {
            PhysicsObject physicsObject = ENTITY_MAP.get(entity);
            ENTITY_MAP.remove(entity);
            COLLISION_MAP.remove(physicsObject.getRigidBody());
        }
    }

    /**
     * Checks if the {@link PhysicsObject} exists in the specific {@link Point}.
     *
     * @param point
     * @return True if the PhysicsObject is at the location
     */
    public boolean existsInInstance(Point point) {
        return POINT_MAP.containsKey(point);
    }

    /**
     * Checks if the {@link PhysicsObject} is linked to the {@link Entity}.
     *
     * @param entity
     * @return True if the PhysicsObject is linked to the entity
     */
    public boolean existsInInstance(Entity entity) {
        return ENTITY_MAP.containsKey(entity);
    }

    public PhysicsSpace getPhysicsSpace() {
        return physicsSpace;
    }

    /**
     * Makes the {@link PhysicsObject} updatable every {@value UPDATABLE_TICK} tick.
     *
     * @param physicsObject
     */
    public void addUpdatablePhysicsObject(PhysicsObject physicsObject) {
        PHYSICS_OBJECTS.add(physicsObject);
    }

    /**
     * No longer makes the {@link PhysicsObject} updatable.
     *
     * @param physicsObject
     */
    public void removeUpdatablePhysicsObject(PhysicsObject physicsObject) {
        PHYSICS_OBJECTS.remove(physicsObject);
    }

    /**
     * Checks if the {@link PhysicsObject} is updatable.
     *
     * @param physicsObject
     * @return True if the PhysicsObject is updatable
     */
    public boolean containsUpdatablePhysicsObject(PhysicsObject physicsObject) {
        return PHYSICS_OBJECTS.contains(physicsObject);
    }

    public List<PhysicsObject> getPhysicsObjects() {
        return PHYSICS_OBJECTS;
    }

    public Instance getInstance() {
        return instance;
    }

}
