package io.github.tblaze;

import com.jme3.system.NativeLibraryLoader;
import io.github.tblaze.event.ContactEndedEvent;
import io.github.tblaze.event.ContactOngoingEvent;
import io.github.tblaze.event.ContactStartedEvent;
import net.minestom.server.instance.Instance;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class RigidBodyPhysics {

    private static final Map<Instance, MinecraftPhysicsHandler> INSTANCE_PHYSICS_MAP = new HashMap<>();

    private RigidBodyPhysics() {

    }

    /**
     * Initializes the bulletJME library to make the custom physics functional.
     * <br>
     * To get the bulletJME native file, go to
     * <a href="https://github.com/stephengold/Libbulletjme/tags">bulletJME's GitHub download page</a>
     * and install one of the files under assets (Linux: .so, Windows: .dll, MacOS: .dylib). If you get the wrong version
     * of the file, there will be a run-time error to show which version is compatiable.
     *
     * @param bulletJMENativeFile
     * @throws FileNotFoundException
     */
    public static void init(File bulletJMENativeFile) throws FileNotFoundException {
        boolean success = NativeLibraryLoader.loadLibbulletjme(true, bulletJMENativeFile, "Release", "Sp");
        if (!success) {
            throw new FileNotFoundException("The native file to load Bullet JME does not exist.");
        }

        // TODO: Is this piece of BoundingBox code necessary?
        // https://github.com/emortaldev/BlockPhysics/blob/4ee60e757d2bf433a86bd19cc53c8e787da39c08/src/main/java/dev/emortal/Main.java#L83


    }

    /**
     * Creates and integrates custom physics into the instance.
     * The boolean arguments are there to disable any listeners that are not necessary for the particular instance.
     *
     * @param instance
     * @param listenToContactEnded If true, {@link ContactEndedEvent} will be called
     * @param listenToContactOngoing If true, {@link ContactOngoingEvent} will be called
     * @param listenToContactStarted If true, {@link ContactStartedEvent} will be called
     * @return
     */
    public static MinecraftPhysicsHandler createPhysics(Instance instance, boolean listenToContactEnded, boolean listenToContactOngoing, boolean listenToContactStarted) {
        var physicsHandler = new MinecraftPhysicsHandler(instance, listenToContactEnded, listenToContactOngoing, listenToContactStarted);
        INSTANCE_PHYSICS_MAP.put(instance, physicsHandler);

        return physicsHandler;
    }

    /**
     * Removes the custom physics from the instance
     *
     * @param instance
     */
    public static void removePhysics(Instance instance) {
        INSTANCE_PHYSICS_MAP.remove(instance);
    }

    public static MinecraftPhysicsHandler getPhysicsHandler(Instance instance) {
        return INSTANCE_PHYSICS_MAP.get(instance);
    }

    /**
     * Returns true if there is custom physics in the instance
     *
     * @param instance
     * @return
     */
    public static boolean hasPhysics(Instance instance) {
        return INSTANCE_PHYSICS_MAP.containsKey(instance);
    }

}
