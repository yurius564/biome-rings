# Biome Rings

This mod aims to provide a json file to force the generation of a biome or a list of biomes in a specified range of the overworld center.

#### This mod **DO NOT** changes the terrain, it only forces the biome settings, what affects mob spawnings, structure generations and natural enviroment (trees, grass, flowers, etc).
#### It only works on new generations, this mod do not overwrites already existing chunks.

---

Note that this mod **was only tested in vanilla 1.21.1, and probably has little to no compatiblity with any other custom biomes/terrain generation mods**.

I (the author) have no plans of updating this mod any further than bugfixes, optimization and/or config file QoL.
If you want to add features or compatibilities **you are strongly incentivated to fork/copy the repository and do your version**.

## How to use / What it can do

> The config file is located in `~/config/biomerings.json`
> 
Note that the config file will be auto-generated with an example code in the config.

the parameters in the config are:
| Key | Description | Default |
| - | - | - |
| roundness | The deformation of the rings, where 1.0 is perfectly circular | 0.7 |
| noise | The repetition of the "stripes" of biome when there are many in the same range, a bigger number means more visible "stripes" | 0.4 |
| global_blacklist | The list of biomes that are not meant to be overrided | [...] |
| rules | The list of rules for the generation | [...] |

---

You can force a list of biomes to appear at certain range of the overworld center, settings minimum and maximum distances and pool weigth.

For example, you can set generation to be plains from 0 to 300, pool desert, badlands and wooded_badlands between 300 and 600, and pool forest and dark_forest between 600 and 900 blocks of distance of the world center.
<details>
<summary>Example of config file</summary>
  
```JSON
{
  "roundness": 0.4,
  "noise": 0.5,
  "global_blacklist": [
    "minecraft:ocean",
    "minecraft:deep_ocean",
    "minecraft:warm_ocean",
    "minecraft:lukewarm_ocean",
    "minecraft:cold_ocean",
    "minecraft:deep_cold_ocean",
    "minecraft:beach",
    "minecraft:river",
    "minecraft:mushroom_fields"
  ],
  "rules": [
    {
      "biome_id": "minecraft:plains",
      "min_distance": 0.0,
      "max_distance": 300.0,
      "probability": 1.0
    },
    {
      "biome_id": "minecraft:desert",
      "min_distance": 300.0,
      "max_distance": 600.0,
      "probability": 0.4
    },
    {
      "biome_id": "minecraft:badlands",
      "min_distance": 300.0,
      "max_distance": 600.0,
      "probability": 0.3
    },
    {
      "biome_id": "minecraft:wooded_badlands",
      "min_distance": 300.0,
      "max_distance": 600.0,
      "probability": 0.3
    },
    {
      "biome_id": "minecraft:forest",
      "min_distance": 600.0,
      "max_distance": 900.0,
      "probability": 0.7
    },
    {
      "biome_id": "minecraft:dark_forest",
      "min_distance": 600.0,
      "max_distance": 900.0,
      "probability": 0.3
    }
  ]
}
```
</details>

You can also overwrite unwanted biomes at range of the worldspawn by setting then as whitelist of whatever you feel like, for example here i overwrite snow biomes with plains and meadow. (also frozen rivers as rivers and etc)
| Before the rule | After the rule |
| --- | --- |
| ![before the rule](https://cdn.modrinth.com/data/cached_images/ba1fe2caad23e7e91758f8eddd21e598e89656b1_0.webp) | ![after the rule](https://cdn.modrinth.com/data/cached_images/8a3f867b0b2dc44bb6e5c750a0ff4ed236b88806_0.webp) |
