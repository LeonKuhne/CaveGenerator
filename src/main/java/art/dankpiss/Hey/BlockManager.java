package art.dankpiss.Hey;
import java.util.HashMap;
import org.bukkit.util.BlockVector;

public class BlockManager<T>
  extends HashMap<BlockVector, T>
  implements Watcher
{

  public static class Action {
    public static String CREATE_BLOCK = "create";
    public static String DESTROY_BLOCK = "destroy";
  }

  @Override
  public void tell(Position pos, String command) {
    if (command.equals(Action.DESTROY_BLOCK)) {
      remove(pos);
    } else if (command.equals(Action.CREATE_BLOCK)) {
      put(pos, (T) pos);
      pos.onCreate();
    }
  }

  public void put(T pos) {
    super.put((BlockVector) pos, pos);
  }
}

