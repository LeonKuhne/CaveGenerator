package art.dankpiss.CaveGenerator;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;

public class CaveChunkGenerator extends ChunkGenerator {
  private BlockVector trader;

  public CaveChunkGenerator(int nSegments) {
    trader = new BlockVector(0, 255, 0);
  }

  //
  // GENERATORS

  @Override
  public void generateNoise(
    WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData
  ) {
    ChunkBuilder builder = new ChunkBuilder(chunkX, chunkZ);
    // create spawn bubble
    int spawnRadius = 7;
    builder.spawn(null, (x, y, z) 
      // distance from spawn is less than radius
      -> trader.distance(new BlockVector(x, y, z)) < spawnRadius
      // height is 0 or greater
      && y >= trader.getBlockY()
    );

    // fill with mud 
    builder.add(Material.PACKED_MUD, (x, y, z) -> true);

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
    ChunkBuilder builder = new ChunkBuilder(chunkX, chunkZ);

    // place down glowstone at trader
    builder.spawn(Material.GLOWSTONE, (x, y, z) 
      -> trader.distance(new BlockVector(x, y+1, z)) < 1);
    builder.build(chunkData);

    // place down fountain at spawn
    // as long as only so many golden apples exist in the world, 3 per player
    // the foundant drops golden apples every 5 seconds
  }

  @Override
  public void generateBedrock(
    WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData
  ) {
    ChunkBuilder builder = new ChunkBuilder(chunkX, chunkZ);
    builder.add(Material.BEDROCK, (x, y, z) -> y == -64 || y == 319);
    builder.build(chunkData);
  }

  //
  // SETTINGS

  @Override
  public Location getFixedSpawnLocation(World world, Random random) {
    return trader.toLocation(world);
  }

  @Override
  public boolean canSpawn(World world, int x, int z) {
    return true;
  }
}