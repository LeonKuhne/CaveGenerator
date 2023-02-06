package art.dankpiss.CaveGenerator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.ChunkGenerator;
import java.util.Random;
import java.util.logging.Logger;

public class CaveChunkGenerator extends ChunkGenerator {
  private BlockData[] layerBlock;
  private int[] layerHeight;
  private boolean newHeight = false;

  CaveChunkGenerator(int nLayers, Logger log) {
    layerBlock = new BlockData[nLayers];
    layerHeight = new int[nLayers];

    for (int layer = 0; layer < nLayers; layer++) {
      BlockData blockData = Material.MUD.createBlockData();

      layerBlock[layer] = blockData;
      layerHeight[layer] = layer;
    }
  }

  @Override
  public ChunkData generateChunkData(
    World world, Random random, int chunkX, int chunkZ, BiomeGrid biome
  ) {
    ChunkData chunk = createChunkData(world);

    int y = 0;
    if (newHeight) {
      y = -64;
    }
    for (int i = 0; i < layerBlock.length; i++) {
      chunk.setRegion(0, y, 0, 16, y + layerHeight[i], 16, layerBlock[i]);
      y += layerHeight[i];
    }

    return chunk;
  }

  @Override
  public Location getFixedSpawnLocation(World world, Random random) {
    if (!world.isChunkLoaded(0, 0)) {
      world.loadChunk(0, 0);
    }

    int highestBlock = world.getHighestBlockYAt(0, 0);
    if ((highestBlock <= 0) && (world.getBlockAt(0, 0, 0).getType() == Material.AIR)) {
      return new Location(world, 0, 64, 0);
    }
    return new Location(world, 0, highestBlock, 0);
  }
}
