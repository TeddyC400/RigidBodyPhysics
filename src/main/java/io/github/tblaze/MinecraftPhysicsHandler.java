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

public class MinecraftPhysicsHandler {

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

            // The 3rd, 4th, and 5th arguments are set to false by default
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
        }).repeat(TaskSchedule.tick(1)).schedule();
    }

    public void update(float delta) {
        if (physicsSpace == null)
            return;

        physicsSpace.update(delta);

        for (var object : PHYSICS_OBJECTS) {
            object.update(delta);
        }
    }

    public boolean hasMinecraftPhysicsObject(PhysicsCollisionObject object) {
        return COLLISION_MAP.containsKey(object);
    }

    public PhysicsObject getFromPoint(Point point) {
        return POINT_MAP.get(point);
    }

    public PhysicsObject getFromEntity(Entity entity) {
        return ENTITY_MAP.get(entity);
    }

    public void addToInstance(Point point, PhysicsObject physicsObject) {
        POINT_MAP.put(point, physicsObject);
        COLLISION_MAP.put(physicsObject.getRigidBody(), physicsObject);
    }

    public void addToInstance(Entity entity, PhysicsObject physicsObject) {
        ENTITY_MAP.put(entity, physicsObject);
        COLLISION_MAP.put(physicsObject.getRigidBody(), physicsObject);
    }

    public void removeFromInstance(Point point) {
        if (POINT_MAP.containsKey(point)) {
            PhysicsObject physicsObject = POINT_MAP.get(point);
            POINT_MAP.remove(point);
            COLLISION_MAP.remove(physicsObject.getRigidBody());
        }
    }

    public void removeFromInstance(Entity entity) {
        if (ENTITY_MAP.containsKey(entity)) {
            PhysicsObject physicsObject = ENTITY_MAP.get(entity);
            ENTITY_MAP.remove(entity);
            COLLISION_MAP.remove(physicsObject.getRigidBody());
        }
    }

    public boolean existsInInstance(Point point) {
        return POINT_MAP.containsKey(point);
    }

    public boolean existsInInstance(Entity entity) {
        return ENTITY_MAP.containsKey(entity);
    }

    public PhysicsSpace getPhysicsSpace() {
        return physicsSpace;
    }

    public void addUpdatablePhysicsObject(PhysicsObject physicsObject) {
        PHYSICS_OBJECTS.add(physicsObject);
    }

    public void removeUpdatablePhysicsObject(PhysicsObject physicsObject) {
        PHYSICS_OBJECTS.remove(physicsObject);
    }

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
