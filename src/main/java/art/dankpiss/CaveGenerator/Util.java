package art.dankpiss.CaveGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockVector;

public class Util {

  private static final Logger logger = Logger.getLogger(
    Color.BG_RED + Color.BLACK + "> " + Color.WHITE + "CAVE WARS"
    + Color.BLACK + " <" + Color.BG_RESET + Color.RESET
  );
  public static final int SEGMENTS = 5;
  public static final List<BukkitTask> tasks = new ArrayList<>();
  public class Color { // auto-generated
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLACK = "\u001B[30m";
    public static final String WHITE = "\u001B[37m";
    public static final String STEEL = "\u001B[38;5;8m";
    // custom color, red background, black red text
    public static final String BG_RESET = "\u001B[49;39m";
    public static final String BG_RED = "\u001B[48;5;88m";
  }

  public interface Conditional {
    public Boolean eval(Integer x, Integer y, Integer z);
  }

  public interface Action {
    public void run(Integer x, Integer y, Integer z);
  }
  public interface BlockAction {
    public void run(Block block);
  }

  private static Plugin plugin;
  private static World caveWorld;

  static void log(String msg) { 
    logger.info(Color.STEEL + msg);
  }

  public static void enable(Plugin plugin) {
    Util.plugin = plugin;
    Util.caveWorld = plugin.getServer().getWorld("world");
  }
  public static void disable() {
    tasks.forEach(BukkitTask::cancel);
  }

  public static Boolean verify() {
    if (plugin == null) {
      Util.log(Color.RED + "Plugin not enabled");
      return false;
    }
    return true;
  }

  public static void dispatch(Runnable runnable, int interval) {
    if (!verify()) { return; }  
    Util.log(Color.GREEN + "Dispatching task");
    plugin.getServer().getScheduler().runTaskTimer(
      plugin, runnable, 0, interval);
  }

  public static Block get(BlockVector block) {
    return caveWorld.getBlockAt(
      block.getBlockX(), block.getBlockY(), block.getBlockZ());
  }

  public static void loop(
    int minX, int maxX,
    int minY, int maxY,
    int minZ, int maxZ,
    Action action
  ) {
    for (int x = minX; x <= maxX; x++) {
      for (int y = minY; y <= maxY; y++) {
        for (int z = minZ; z <= maxZ; z++) {
          action.run(x, y, z);
        }
      }
    }
  }

  public static void near(Action action) {
    loop(-1, 1, -1, 1, -1, 1, action);
  }

  public static void neighbors(Block block, BlockAction action) {
    // loop through block faces
    for (int i = 0; i < 6; i++) {
      BlockFace face = BlockFace.values()[i];
      Block neighbor = block.getRelative(face);
      action.run(neighbor);
    }
  }

  public static void neighbors(
    BlockVector pos, int count, Action action
  ) {
    // loop through block faces
    int x = pos.getBlockX();
    int y = pos.getBlockY();
    int z = pos.getBlockZ();
    action.run(x + 1, y, z);
    action.run(x - 1, y, z);
    if (count > 4) { action.run(x, y - 1, z); }
    if (count > 5) { action.run(x, y + 1, z); }
    action.run(x, y, z + 1);
    action.run(x, y, z - 1);
  }

  public static void neighbors(BlockVector pos, Action action) {
    neighbors(pos, 6, action);
  }

  public static void neighbors(BlockVector pos, int count, BlockAction action) {
    neighbors(pos, count, (x, y, z) -> {
      action.run(get(new BlockVector(x, y, z)));
    });
  }

  public static Block getRelativeBlock(
    BlockVector block, int x, int y, int z
  ) {
    return caveWorld.getBlockAt(
      block.getBlockX() + x,
      block.getBlockY() + y,
      block.getBlockZ() + z
    );
  }

  public static BlockVector pos(Block block) {
    return new BlockVector(
      block.getX(),
      block.getY(),
      block.getZ()
    );
  }

  public static Set<BlockVector> flow(BlockVector pos) {
    Set<BlockVector> list = new HashSet<>();
    int x = pos.getBlockX();
    int y = pos.getBlockY();
    int z = pos.getBlockZ();
    list.add(new BlockVector(x + 1, y, z));
    list.add(new BlockVector(x - 1, y, z));
    list.add(new BlockVector(x, y - 1, z));
    list.add(new BlockVector(x, y, z + 1));
    list.add(new BlockVector(x, y, z - 1));
    return list;
  }

  public static Block at(BlockVector pos) {
    return caveWorld.getBlockAt(
      pos.getBlockX(),
      pos.getBlockY(),
      pos.getBlockZ()
    );
  }
}
