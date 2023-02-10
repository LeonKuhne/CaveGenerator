package art.dankpiss.CaveGenerator;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.util.BlockVector;

import art.dankpiss.CaveGenerator.Util.Conditional;

public class ChunkBuilder {
  Map<Material, Conditional>  conditions;
  private int chunkX;
  private int chunkZ;
  public ChunkBuilder(int chunkX, int chunkZ) {
    // map materials to conditional callbacks
    conditions = new HashMap<>();
    this.chunkX = chunkX;
    this.chunkZ = chunkZ;
  }

  // add material to gradient 
  public void add(Material material, Conditional conditional) {
    conditions.put(material, conditional);
  }

  // same as add but only for spawn chunks
  public void spawn(Material material, Conditional condtional) {
    if (chunkX >= -1 && chunkX <= 1 && chunkZ >= -1 && chunkZ <= 1) {
      add(material, condtional);
    }
  }

  // build chunk
  public void build(ChunkData chunkData) {
    int x0 = chunkX * 16;
    int z0 = chunkZ * 16;

    Util.loop(
      0, 16, 
      -64, 320, 
      0, 16, 
      (x, y, z) -> {
      Material material = get(x0 + x, y, z0 + z);
      if (material != null) {
        chunkData.setBlock(x, y, z, material);
      }
    });
  }

  private Material get(int x, int y, int z) {
    // find first valid material in gradient
    BlockVector vector = new BlockVector(x, y, z);
    for (Map.Entry<Material, Conditional> entry : conditions.entrySet()) {
      if (entry.getValue().eval(vector)) {
        return entry.getKey();
      }
    }
    return null;
  }
}