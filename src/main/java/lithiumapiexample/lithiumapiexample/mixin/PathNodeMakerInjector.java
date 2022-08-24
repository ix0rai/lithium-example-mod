package lithiumapiexample.lithiumapiexample.mixin;

import lithiumapiexample.lithiumapiexample.LithiumApiExample;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * make mobs avoid the block when lithium is not installed
 */
@Mixin(LandPathNodeMaker.class)
public class PathNodeMakerInjector {
    @Inject(method="getCommonNodeType", at = @At("HEAD"), cancellable = true)
    private static void getCommonNodeType(BlockView blockView, BlockPos blockPos, CallbackInfoReturnable<PathNodeType> callbackInfoReturnable) {
        if (blockView.getBlockState(blockPos).isOf(LithiumApiExample.DAMAGING_BLOCK)) {
            callbackInfoReturnable.setReturnValue(PathNodeType.DAMAGE_OTHER);
        }
    }
}
