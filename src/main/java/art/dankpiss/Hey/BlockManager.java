package art.dankpiss.Hey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.util.BlockVector;

public class BlockManager<T>
  extends HashMap<BlockVector, T>
  implements Watcher<T>
{

  public static class Action {
    public static String CREATE_BLOCK = "create";
    public static String DESTROY_BLOCK = "destroy";
  }
  private List<T> toBeDeleted = new ArrayList<>();
  private List<T> toBeCreated = new ArrayList<>();

  @Override
  public void delete(T pos) {
    toBeDeleted.add((T) pos);
  }

  @Override
  public void create(T pos) {
    toBeCreated.add((T) pos);
  }

  // apply changes
  public void cleanup() {
    // delete queued
    toBeDeleted.stream()
      .filter(pos -> containsKey((BlockVector) pos))
      .forEach(pos -> remove((BlockVector) pos));
    toBeDeleted.clear();
    // create queued
    toBeCreated.stream()
      .filter(pos -> !containsKey((BlockVector) pos))
      .forEach(pos -> put((BlockVector) pos, pos));
    toBeCreated.clear();
  }

  public void loop(Consumer<T> consumer) {
    for (T block : values()) {
      consumer.accept(block);
    }
    cleanup();
  }
}