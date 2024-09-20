package io.github.tblaze.math;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public class MathUtil {

    /**
     * Converts from {@link Vector3f} to {@link Vec}.
     *
     * @param vector3
     * @return Vec object
     */
    public static Vec toVec(Vector3f vector3) {
        return new Vec(vector3.x, vector3.y, vector3.z);
    }

    /**
     * Converts from {@link Vector3f} to {@link Pos}.
     *
     * @param vector3
     * @return Pos object
     */
    public static Pos toPos(Vector3f vector3) {
        return new Pos(vector3.x, vector3.y, vector3.z);
    }

    /**
     * Converts from {@link Point} to {@link Vector3f}.
     *
     * @param vec
     * @return Vector3f object
     */
    public static Vector3f toVector3(Point vec) {
        return new Vector3f((float)vec.x(), (float)vec.y(), (float)vec.z());
    }

    /**
     * Converts from {@link Quaternion} to float[].
     *
     * @param rotation
     * @return float[] -> [x, y, z, w]
     */
    public static float[] toFloats(Quaternion rotation) {
        return new float[] {
                rotation.getX(),
                rotation.getY(),
                rotation.getZ(),
                rotation.getW()
        };
    }

}
