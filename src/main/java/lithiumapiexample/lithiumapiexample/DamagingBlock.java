package lithiumapiexample.lithiumapiexample;

import me.jellysquid.mods.lithium.api.pathing.BlockPathingBehavior;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DamagingBlock extends Block implements BlockPathingBehavior {
    private static final float MINIMUM_DAMAGE_DISTANCE = 0.003f;

    public DamagingBlock(Settings settings) {
        super(settings);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        // if the entity is both alive and moved, damage it
        if (entity instanceof LivingEntity && !(world.isClient) && movedMinDistance(entity)) {
            entity.damage(DamageSource.MAGIC, 1.0f);
        }
    }

    private boolean movedMinDistance(Entity entity) {
        // the entity must move a minimum distance to be damaged
        // this is implemented so if you accidentally touch the keyboard for a millisecond, you won't be damaged
        return !(entity.lastRenderX == entity.getX() && entity.lastRenderZ == entity.getZ())
                && (Math.abs(entity.getX() - entity.lastRenderX) >= MINIMUM_DAMAGE_DISTANCE
                || Math.abs(entity.getZ() - entity.lastRenderZ) >= MINIMUM_DAMAGE_DISTANCE);
    }

    @Override
    public PathNodeType getPathNodeType(BlockState state) {
        return PathNodeType.DAMAGE_OTHER;
    }

    @Override
    public PathNodeType getPathNodeTypeAsNeighbor(BlockState state) {
        return PathNodeType.WALKABLE;
    }
}
