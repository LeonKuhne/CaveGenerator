package art.dankpiss.Hey;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

// A simple observable
public class Position<T> extends BlockVector {
  private Watcher<T> manager;
  private T that;

  public Position(BlockVector vector) {
    super(vector);
  }

  public void watch(Watcher<T> me, T that) { 
    manager = me;
    this.that = that;
    create();
  }

  public void create() { manager.create(that); }
  public void delete() { manager.delete(that); }
  public void onCleanup() {}

  @Override
  public String toString() {
    return Position.key(this);
  }
  public static String key(int x, int y, int z) {
    return x + "," + y + "," + z;
  }
  public static String key(Block block) {
    return key(block.getX(), block.getY(), block.getZ());
  }
  public static String key(BlockVector vector) {
    return key(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
  }
}