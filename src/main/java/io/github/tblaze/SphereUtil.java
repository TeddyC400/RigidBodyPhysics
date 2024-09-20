package io.github.tblaze;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public final class SphereUtil {

    private SphereUtil() {

    }


    public static List<WorldBlock> getNearbyBlocks(Point position, Instance instance, Set<Point> blocksInSphere, Predicate<WorldBlock> predicate) {
        List<WorldBlock> filteredBlocks = new ArrayList<>();
        Point blockPos;
        Block currentBlock;
        WorldBlock worldBlock;
        for (Point block : blocksInSphere) {
            blockPos = block.add(position);
            // TODO: Might have to be try-catch
            // https://github.com/emortaldev/BlockPhysics/blob/4ee60e757d2bf433a86bd19cc53c8e787da39c08/src/main/java/dev/emortal/SphereUtil.java#L20
            currentBlock = instance.getBlock(blockPos, Block.Getter.Condition.TYPE);

            worldBlock = new WorldBlock(blockPos, currentBlock);
            if (!predicate.test(worldBlock))
                continue;

            filteredBlocks.add(worldBlock);
        }

        return filteredBlocks;
    }

    /**
     * Gets points within a spherical radius
     *
     * @param radius
     * @return
     */
    public static Set<Point> getBlocksInSphere(double radius) {
        Set<Point> points = new HashSet<>();

        for (double x = -radius; x <= radius; x++) {
            for (double y = -radius; y <= radius; y++) {
                for (double z = -radius; z <= radius; z++) {
                    if ((x * x) + (y * y) + (z * z) > radius * radius)
                        continue;
                    points.add(new Vec(x, y, z));
                }
            }
        }

        return points;
    }

}
