package art.dankpiss.CaveGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator.ChunkData;

public class ChunkBuilder {
  Map<Material, Function<Integer, Function<Integer, Function<Integer, Boolean>>>>  conditions;
  public ChunkBuilder() {
    // map materials to conditional callbacks
    conditions = new HashMap<>();
  }

  // add material to gradient 
  public void add(
    Material material,
    Function<Integer, Function<Integer, Function<Integer,Boolean>>> conditional
  ) { conditions.put(Material.AIR, conditional); } // forehead

  // build chunk
  public void build(ChunkData chunkData) {
    for (int x = 0; x < 16; x++) {
      for (int z = 0; z < 16; z++) {
        for (int y = -64; y < 320; y++) {
          Material material = get(x, y, z);
          if (material != null) {
            chunkData.setBlock(x, y, z, material);
          }
        }
      }
    }
  }

  // get material from gradient
  private Material get(int x, int y, int z) {
    for (Map.Entry<Material, Function<Integer, Function<Integer, Function<Integer, Boolean>>>> entry : conditions.entrySet()) {
      if (entry.getValue().apply(x).apply(y).apply(z)) {
        return entry.getKey();
      }
    }
    return null;
  }
}