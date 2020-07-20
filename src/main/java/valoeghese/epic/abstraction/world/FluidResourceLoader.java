package valoeghese.epic.abstraction.world;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import valoeghese.epic.Setup;
import valoeghese.epic.abstraction.world.Fluid.Impl;

// adapted from the hallow
public class FluidResourceLoader implements SimpleSynchronousResourceReloadListener {
	private static final ResourceLocation WATER_STILL = new ResourceLocation("minecraft", "block/water_still");
	private static final ResourceLocation WATER_FLOWING = new ResourceLocation("minecraft", "block/water_flow");

	@Override
	public ResourceLocation getFabricId() {
		return new ResourceLocation(Setup.MODID, "fluid");
	}

	private static final FluidRenderHandler CUSTOM = new FluidRenderHandler() {
		@Override
		public TextureAtlasSprite[] getFluidSprites(BlockAndTintGetter view, BlockPos pos, FluidState state) {
			return new TextureAtlasSprite[]{
					Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(WATER_STILL),
					Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(WATER_FLOWING)
			};
		}

		public int getFluidColor(BlockAndTintGetter view, BlockPos pos, FluidState state) {
			Fluid fluid = state.getType();

			if (fluid instanceof Impl) {
				return ((Impl) fluid).fluid.getColour();
			} else {
				throw new RuntimeException("Cannot use Epic Fantasy Custom Fluid Rendering on non epic fantasy fluids.");
			}
		};
	};

	private static List<Impl> customFluids = new ArrayList<>();

	public static void addFluid(Impl impl) {
		customFluids.add(impl);
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		for (Impl impl : customFluids) {
			FluidRenderHandlerRegistry.INSTANCE.register(impl, CUSTOM);
		}
	}
}
