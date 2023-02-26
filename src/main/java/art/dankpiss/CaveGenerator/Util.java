package art.dankpiss.CaveGenerator;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockVector;
import art.dankpiss.Hey.BlockManager;

public class Util {

  // 
  // EXTERNAL

  public static Plugin plugin;
  public static World caveWorld;
  public static Erode erosion;
  public static Server server;
  public static Render render;
  public static final int SEGMENTS = 5;
  public static class DegradeConfig {
    public static double damage = 5.0;
    public static double down_likeliness = 0.01;
    public static double destroyed_per_tick = 0.001;
    public static double level_boundary = 5. / 8.;
    public static double friction_damage = 0.08;
    public static int erosion_radius = 3;
    public static double randomness = 0.1;
  }
  public class Color { // auto-generated
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLACK = "\u001B[30m";
    public static final String WHITE = "\u001B[37m";
    public static final String STEEL = "\u001B[38;5;8m";
    public static final String BG_RESET = "\u001B[49;39m";
    public static final String BG_RED = "\u001B[48;5;88m";
  }

  //
  // INTERNAL

  private static final Logger logger = Logger.getLogger(
    Color.BG_RED + Color.BLACK + "> " + Color.WHITE + "CAVE WARS"
    + Color.BLACK + " <" + Color.BG_RESET + Color.RESET
  );
  private static final List<BukkitTask> tasks = new ArrayList<>();

  //
  // INTERFACES

  public interface Conditional {
    public Boolean eval(BlockVector pos);
  }
  public interface Range {
    public Boolean test(Double x, Double y);
  }
  public interface Action {
    public void run(Integer x, Integer y, Integer z);
  }
  public interface Callback<T> {
    public void run(T result);
  }
  public interface BlockAction {
    public void run(Block block);
  }
  public interface CallbackReturn<T> {
    public T run();
  }

  //
  // CONSTRUCTOR

  public static void enable(Plugin plugin) {
    Util.plugin = plugin;
    server = plugin.getServer();
    Util.caveWorld = server
      .createWorld(new WorldCreator("caves")
        .generator(new CaveChunkGenerator(5)));
    // register erode as a listener
    Util.erosion = new Erode();
    server
      .getPluginManager()
      .registerEvents(erosion, Util.plugin);
    Util.render = new Render();
  }

  public static void disable() {
    tasks.forEach(BukkitTask::cancel);
  }

  //
  // HELPERS

  static void friendlyColors() {
    // replace color codes with bukkit chat colors
    for(Field field : Color.class.getDeclaredFields()) {
      try {
        String color = (String) field.get(null);
        String friendly = color
          .replace("\u001B[", "&")
          .replace("m", "");
        friendly = friendly.substring(0, friendly.length() - 1);
        friendly += "m";
        field.set(null, friendly);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  static void log(String msg) { 
    logger.info(Color.STEEL + msg);
    // DEBUG
    if (server != null) { 
      // replace color codes with bukkit chat colors
      server.broadcastMessage(msg);
    }
  }

  public static void dispatch(Runnable runnable, int interval) {
    Util.log(Color.GREEN + "Dispatching task");
    server.getScheduler().runTaskTimer(
      plugin, runnable, 0, interval);
  }

  // 
  // WORLD HELPERS

  public static Boolean inCave(Block block) {
    return block.getWorld().equals(caveWorld); 
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
      action.run(at(new BlockVector(x, y, z)));
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

  // get the 6 adjacent blocks
  public static Set<BlockVector> star(BlockVector pos) {
    // check if block below is air
    Set<BlockVector> list = new HashSet<>();
    int x = pos.getBlockX();
    int y = pos.getBlockY();
    int z = pos.getBlockZ();
    list.add(new BlockVector(x + 1, y, z));
    list.add(new BlockVector(x - 1, y, z));
    list.add(new BlockVector(x, y + 1, z));
    list.add(new BlockVector(x, y - 1, z));
    list.add(new BlockVector(x, y, z + 1));
    list.add(new BlockVector(x, y, z - 1));
    return list;
  }

  public static Set<BlockVector> nearbyMud(BlockVector vector) {
    return star(vector).stream()
      .filter(Util::isMud)
      .collect(Collectors.toSet());
  }

  public static Set<Degradable> registerNearbyMud(
    BlockManager<Degradable> manager,
    BlockVector origin
  ) {
    return nearbyMud(origin).stream()
      // mark degrading
      .map(vector -> manager.getOrMake(vector, () -> {
        return new Degradable(manager, vector);
      }))
      .collect(Collectors.toSet());
  }

  public static Set<Degradable> registerNearbyMud(
    BlockManager<Degradable> manager,
    BlockVector origin, 
    int depth
  ) {
    if (depth == 0) { return new HashSet<>(); }
    Set<Degradable> origins = registerNearbyMud(manager, origin);
    Set<Degradable> muds = new HashSet<>(origins);
    origins.forEach(degradable -> {
      muds.addAll(registerNearbyMud(manager, degradable, depth - 1));
    });
    return muds;
  }

  private static boolean isMud(BlockVector vector) {
    List<Material> muds = Arrays.asList(Material.MUD, Material.PACKED_MUD);
    Material mat = at(vector).getType();
    return muds.contains(mat);
  }

  public static Block at(BlockVector pos) {
    return caveWorld.getBlockAt(
      pos.getBlockX(),
      pos.getBlockY(),
      pos.getBlockZ()
    );
  }

  public static boolean crossThresholdUp(double before, double after, double threshold) {
    return (before < threshold && after >= threshold); 
  }
  public static boolean crossThresholdDown(double before, double after, double threshold) {
    return crossThresholdUp(after, before, threshold);
  }

  public static Boolean placeFalling(BlockVector vector, Material material) {
    // get block below
    Block below = at(vector).getRelative(BlockFace.DOWN);
    // check if block below is solid
    switch (below.getType()) {
      case WATER:
        return false;
      // recurse
      case AIR:
        return placeFalling(pos(below), material);
      default:
        Util.at(vector).setType(material);
        return true;
    }
  }
}
