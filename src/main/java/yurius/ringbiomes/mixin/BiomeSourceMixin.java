package yurius.ringbiomes.mixin;

import yurius.ringbiomes.ModConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.ArrayList;
import java.util.List;

@Mixin(MultiNoiseBiomeSource.class)
public class BiomeSourceMixin {

  @Inject(method = "getNoiseBiome", at = @At("RETURN"), cancellable = true)
  private void onGetNoiseBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
    Holder<Biome> originalBiomeHolder = cir.getReturnValue();
    if (originalBiomeHolder == null) return;
		
		boolean isOverworld = false;
		for (Holder<Biome> holder : ((MultiNoiseBiomeSource)(Object)this).possibleBiomes()) {
			if (holder.unwrapKey().isPresent()) {
				String path = holder.unwrapKey().get().location().getPath();
				if (path.equals("plains")) {
					isOverworld = true;
					break;
				}
			}
		}
		if (!isOverworld) return;

    String originalBiomeId = "";
    if (originalBiomeHolder.unwrapKey().isPresent()) {
      originalBiomeId = originalBiomeHolder.unwrapKey().get().location().toString();
    }

    if (ModConfig.INSTANCE != null && ModConfig.INSTANCE.global_blacklist != null) {
      if (ModConfig.INSTANCE.global_blacklist.contains(originalBiomeId)) return; 
    }

    int blockX = x * 4;
    int blockZ = z * 4;

    double realDistance = Math.sqrt((blockX * blockX) + (blockZ * blockZ));
    double theta = Math.atan2(blockZ, blockX);

    double noiseDisplacement = 0;
    noiseDisplacement += 85.0 * Math.sin(1 * theta + 0.5) + 65.0 * Math.cos(1 * theta - 0.2);
    noiseDisplacement += 45.0 * Math.sin(2 * theta + 1.2) + 35.0 * Math.cos(2 * theta + 0.8);
    noiseDisplacement += 22.0 * Math.sin(4 * theta - 0.5) + 18.0 * Math.cos(4 * theta + 2.1);
    noiseDisplacement += 10.0 * Math.sin(8 * theta + 3.14);

    float roundnessConfig = (ModConfig.INSTANCE != null) ? ModConfig.INSTANCE.roundness : 0.7f;
    
    // Linearly mapping 1.0 roundness -> 0.0 intensity, and 0.0 roundness -> 1.0 intensity
    // This allows low roundness configurations to function safely without over-scaling
    double deformationIntensity = Math.max(0.0, 1.0 - roundnessConfig);

    double distanceMultiplier = Math.min(1.0, realDistance / 300.0); 
    double adjustedDistance = realDistance - (noiseDisplacement * distanceMultiplier * deformationIntensity);

    // FIX: Ensure adjustedDistance is never negative so it always satisfies the 0.0 minimum distance rule
    if (adjustedDistance < 0.0) {
      adjustedDistance = 0.0;
    }

    if (ModConfig.INSTANCE != null && ModConfig.INSTANCE.rules != null) {
      List<ModConfig.BiomeRule> activeRules = new ArrayList<>();
      float totalWeight = 0.0f;

      for (ModConfig.BiomeRule rule : ModConfig.INSTANCE.rules) {
        if(rule.whitelist != null && !rule.whitelist.contains(originalBiomeId)) continue;
        if (adjustedDistance >= rule.min_distance && adjustedDistance <= rule.max_distance) {
          activeRules.add(rule);
          totalWeight += rule.probability;
        }
      }

      if (!activeRules.isEmpty() && totalWeight > 0.0f) {
        float noiseConfig = (ModConfig.INSTANCE != null) ? ModConfig.INSTANCE.noise : 1.0f;

        double sampleTemp = sampler.sample(x, y, z).temperature() / 10000.0;
        double sampleHumid = sampler.sample(x, y, z).humidity() / 10000.0;

        double rawNoise = Mth.frac((sampleTemp * 2.00 * noiseConfig) + (sampleHumid * 3.00 * noiseConfig));
        float deterministicRoll = (float) (rawNoise * totalWeight);

        float currentWeightSum = 0.0f;
        for (ModConfig.BiomeRule rule : activeRules) {
          currentWeightSum += rule.probability;
          if (deterministicRoll <= currentWeightSum) {
            ResourceLocation targetLocation = ResourceLocation.tryParse(rule.biome_id);
            if (targetLocation != null) {
              
              for (Holder<Biome> holder : ((MultiNoiseBiomeSource)(Object)this).possibleBiomes()) {
                if (holder.unwrapKey().isPresent()) {
                  ResourceLocation checkLoc = holder.unwrapKey().get().location();
                  if (checkLoc.equals(targetLocation)) {
                    cir.setReturnValue(holder);
                    return;
                  }
                }
              }
            }
            break;
          }
        }
      }
    }
  }
}
