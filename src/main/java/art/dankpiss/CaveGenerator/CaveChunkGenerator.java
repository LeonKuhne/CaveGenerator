package art.dankpiss.CaveGenerator;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;

public class CaveChunkGenerator extends ChunkGenerator {
  private BlockVector spawn;
  private BlockVector trader;
  private List<BlockVector> segments;

  public CaveChunkGenerator(int nSegments) {
    spawn = new BlockVector(0, 0, 0);

    // spawn trader at random direction with height of 255
    Double dir = Math.random() * 2 * Math.PI;
    Double x = Math.cos(dir) * 1000;
    Double z = Math.sin(dir) * 1000;
    trader = new BlockVector(x, 255, z);

    // segments from spawn to trader
    // TODO
  }

  @Override
  public void generateNoise(
    WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData
  ) {
    ChunkBuilder builder = new ChunkBuilder();
    if (chunkX >= -1 && chunkX <= 1 && chunkZ >= -1 && chunkZ <= 1) {

      // create spawn bubble
      int spawnRadius = 5;
      builder.add(Material.AIR, (x) -> (y) -> (z) -> 
        // distance from spawn is less than radius
        spawn.distance(new BlockVector(x + chunkX * 16, y, z + chunkZ * 16)) < spawnRadius
        // height is above 0
        && y > 0
      );
    }

    // fill with mud 
    builder.add(Material.PACKED_MUD, (x) -> (y) -> (z) -> true);

    // generate chunks
    builder.build(chunkData);

    // carve path to trader using segmented lines
    // TODO
  }

  // place down golden apple fountain
  @Override
  public void generateSurface(
    WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData
  ) {
    ChunkBuilder builder = new ChunkBuilder();

    // place down glowstone at spawn
    builder.add(Material.GLOWSTONE, x -> y -> z -> 
      x + chunkX * 16 == spawn.getBlockX() && z + chunkZ * 16 == spawn.getBlockZ()
    );
    builder.build(chunkData);

    // place down golden apple fountain
    // TODO
  }

  @Override
  public void generateBedrock(
    WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData
  ) {
    ChunkBuilder builder = new ChunkBuilder();
    builder.add(Material.BEDROCK, x -> y -> z -> y == -64 || y == 319);
    builder.build(chunkData);
  }

  @Override
  public Location getFixedSpawnLocation(World world, Random random) {
    return spawn.toLocation(world);
  }

  @Override
  public boolean canSpawn(World world, int x, int z) {
    return true;
  }
}