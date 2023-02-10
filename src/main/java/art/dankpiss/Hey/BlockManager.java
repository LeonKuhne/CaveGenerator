package art.dankpiss.Hey;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import art.dankpiss.CaveGenerator.Util;

public class BlockManager<T>
  extends HashMap<String, T>
  implements Watcher<T>
{
  public static class Action {
    public static String CREATE_BLOCK = "create";
    public static String DESTROY_BLOCK = "destroy";
  }
  private Set<T> toBeDeleted = new HashSet<>();
  private Set<T> toBeCreated = new HashSet<>();

  @Override
  public void delete(T pos) {
    toBeDeleted.add((T) pos);
  }

  @Override
  public void create(T pos) {
    toBeCreated.add((T) pos);
  }

  // apply changes
  public int cleanup() {
    // create queued
    for (T pos : toBeCreated) {
      String key = pos.toString();
      if (containsKey(key)) { continue; }
      put(key, pos);
    }
    toBeCreated.clear();
    // delete queued
    for (T pos : toBeDeleted) {
      String key = pos.toString();
      if (!containsKey(key)) { continue; }
      remove(key);
    }
    int nDeleted = toBeDeleted.size();
    toBeDeleted.clear();
    return nDeleted;
  }

  public void loop(Consumer<T> consumer) {
    for (T block : values()) {
      consumer.accept(block);
    }
    cleanup();
  }

  public T get(Block block) {
    return get(Util.key(block));
  }
  public T get(BlockVector vector) {
    return get(Util.key(vector));
  }

  public Boolean has(BlockVector vector) {
    return containsKey(Util.key(vector));
  }
}