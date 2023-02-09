package art.dankpiss.Hey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.util.BlockVector;

public class BlockManager<T>
  extends HashMap<BlockVector, T>
  implements Watcher
{

  public static class Action {
    public static String CREATE_BLOCK = "create";
    public static String DESTROY_BLOCK = "destroy";
  }
  private List<Position> toBeDeleted = new ArrayList<>();
  private List<Position> toBeCreated = new ArrayList<>();

  @Override
  public void tell(Position pos, String command) {
    if (command.equals(Action.DESTROY_BLOCK)) {
      // enque this for after the loop
      toBeDeleted.add(pos);
      pos.onDestroy();
    } else if (command.equals(Action.CREATE_BLOCK)) {
      toBeCreated.add(pos);
      pos.onCreate();
    }
  }

  // apply changes
  public void cleanup() {
    // remove old
    System.out.println("Cleaning up " + toBeDeleted.size() + " blocks");
    toBeDeleted.forEach(this::remove);
    toBeDeleted.clear();
    // create new
    System.out.println("Creating " + toBeCreated.size() + " blocks");
    toBeCreated.forEach(this::put);
    toBeCreated.clear();
  }

  public void put(Position pos) {
    super.put((BlockVector) pos, (T) pos);
  }
}

