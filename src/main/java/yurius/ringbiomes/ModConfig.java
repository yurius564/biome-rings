package yurius.ringbiomes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModConfig {
	private static final File FILE = new File("config/biomerings.json");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static ConfigInstance INSTANCE = new ConfigInstance();

	public static class BiomeRule {
		public String biome_id;
		public double min_distance;
		public double max_distance;
		public float probability;
		public List<String> whitelist;
	}

	public static class ConfigInstance {
		// New configuration options
		public float roundness = 0.7f;
		public float noise = 0.4f;
		
		public List<String> global_blacklist = new ArrayList<>();
		public List<BiomeRule> rules = new ArrayList<>();
	}

	public static void load() {
		if (!FILE.getParentFile().exists()) FILE.getParentFile().mkdirs();
		if (!FILE.exists()) {
			// Setup default config values
			INSTANCE.roundness = 0.7f;
			INSTANCE.noise = 0.4f;

			// Setup global blacklisted biomes
			INSTANCE.global_blacklist.add("minecraft:ocean");
			INSTANCE.global_blacklist.add("minecraft:deep_ocean");
			INSTANCE.global_blacklist.add("minecraft:warm_ocean");
			INSTANCE.global_blacklist.add("minecraft:lukewarm_ocean");
			INSTANCE.global_blacklist.add("minecraft:cold_ocean");
			INSTANCE.global_blacklist.add("minecraft:deep_cold_ocean");
			INSTANCE.global_blacklist.add("minecraft:beach");
			INSTANCE.global_blacklist.add("minecraft:river");
			INSTANCE.global_blacklist.add("minecraft:mushroom_fields");

			// Setup a default rule
			BiomeRule example = new BiomeRule();
			example.biome_id = "minecraft:plains";
			example.min_distance = 0.0;
			example.max_distance = 800.0;
			example.probability = 1.0f;
			example.whitelist = Arrays.asList(
				"minecraft:snowy_plains", "minecraft:snowy_taiga",
				"minecraft:snowy_slopes", "minecraft:grove"
			);
			
			INSTANCE.rules.add(example);
			save();
			return;
		}
		try (FileReader reader = new FileReader(FILE)) {
			INSTANCE = GSON.fromJson(reader, ConfigInstance.class);
			
			// Fallback defaults for existing files lacking the new parameters
			if (INSTANCE.roundness == 0.0f && INSTANCE.noise == 0.0f) {
				INSTANCE.roundness = 0.7f;
				INSTANCE.noise = 0.4f;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void save() {
		try (FileWriter writer = new FileWriter(FILE)) {
			GSON.toJson(INSTANCE, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
