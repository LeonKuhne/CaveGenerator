package art.dankpiss.Hey;
import org.bukkit.util.BlockVector;

import art.dankpiss.CaveGenerator.Util;

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

  @Override
  public String toString() {
    return Util.key(getBlockX(), getBlockY(), getBlockZ());
  }
}