package art.dankpiss.Hey;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.util.BlockVector;
import art.dankpiss.CaveGenerator.Util.CallbackReturn;

public class BlockManager<T> implements Watcher<T> {
  public static class Action {
    public static String CREATE_BLOCK = "create";
    public static String DESTROY_BLOCK = "destroy";
  }
  private Map<String, T> toBeDeleted = new HashMap<>();
  private Map<String, T> toBeCreated = new HashMap<>();
  private Map<String, T> map = new HashMap<>();

  @Override
  public void delete(T pos) {
    toBeDeleted.put(pos.toString(), pos);
  }

  @Override
  public void create(T pos) {
    toBeCreated.put(pos.toString(), pos);
  }

  // apply changes
  public int cleanup() {
    // create queued
    for (T pos : toBeCreated.values()) {
      String key = pos.toString();
      if (map.containsKey(key)) { continue; }
      map.put(key, pos);
    }
    toBeCreated.clear();
    // delete queued
    int nDeleted = 0;
    for (T pos : toBeDeleted.values()) {
      String key = pos.toString();
      if (!map.containsKey(key)) { continue; }
      map.remove(key);
      nDeleted++;
    }
    toBeDeleted.clear();
    // return delta
    return nDeleted;
  }

  // loop without modifiying
  public void tap(Consumer<T> consumer) {
    for (T block : map.values()) {
      consumer.accept(block);
    }
  }

  // returns number of deleted blocks
  public int loop(Consumer<T> consumer) {
    tap(consumer);
    return cleanup();
  }

  public T get(String key) {
    return map.getOrDefault(key, null);
  }

  public int size() {
    return map.size();
  }

  public boolean has(String key) {
    return map.containsKey(key);
  }
  public boolean has(Position<T> pos) {
    return has(pos.toString());
  }

  public T getOrMake(BlockVector vector, CallbackReturn<T> create) {
    String key = Position.key(vector);
    if (has(key)) {
      return get(key);
    } else {
      return create.run();
    }
  }
}