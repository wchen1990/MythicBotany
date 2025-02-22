package mythicbotany.functionalflora.base;

import io.github.noeppi_noeppi.libx.base.tile.BlockBE;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import vazkii.botania.xplat.BotaniaConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class BlockFloatingFunctionalFlower<T extends FunctionalFlowerBase> extends BlockBE<T> {

    private static final VoxelShape SHAPE = box(1.6D, 1.6D, 1.6D, 14.4D, 14.4D, 14.4D);

    private final BlockFunctionalFlower<T> nonFloatingBlock;

    public BlockFloatingFunctionalFlower(ModX mod, Class<T> beClass, BlockFunctionalFlower<T> nonFloatingBlock) {
        super(mod, beClass, Properties.of(Material.PLANT).isRedstoneConductor((state, world, pos) -> false)
                .instabreak().sound(SoundType.GRASS));
        this.nonFloatingBlock = nonFloatingBlock;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        BlockEntityRenderers.register(this.getBlockEntityType(), mgr -> new RenderFunctionalFlower<>());
        ItemBlockRenderTypes.setRenderLayer(this, RenderType.cutoutMipped());
    }

    public BlockFunctionalFlower<T> getNonFloatingBlock() {
        return this.nonFloatingBlock;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE;
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public boolean hasAnalogOutputSignal(@Nonnull BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getAnalogOutputSignal(@Nonnull BlockState blockState, @Nonnull Level level, @Nonnull BlockPos pos) {
        FunctionalFlowerBase te = this.getBlockEntity(level, pos);
        if (te.getCurrentMana() > 0) {
            return 1 + (int) ((te.getCurrentMana() / (double) te.maxMana) * 14);
        } else {
            return 0;
        }
    }

    public boolean propagatesSkylightDown(BlockState state, @Nonnull BlockGetter reader, @Nonnull BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (this.getNonFloatingBlock().isGenerating) {
            tooltip.add(new TranslatableComponent("botania.flowerType.generating").withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));

        } else {
            tooltip.add(new TranslatableComponent("botania.flowerType.functional").withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));

        }
        //noinspection ConstantConditions
        tooltip.add(new TranslatableComponent("block." + this.mod.modid + "." + this.getNonFloatingBlock().getRegistryName().getPath() + ".description").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean useShapeForLightOcclusion(@Nonnull BlockState state) {
        return true;
    }

    @Nonnull
    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(@Nonnull BlockState state) {
        if (FMLEnvironment.dist != Dist.CLIENT) return RenderShape.ENTITYBLOCK_ANIMATED;
        return BotaniaConfig.client().staticFloaters() ? RenderShape.MODEL : RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
