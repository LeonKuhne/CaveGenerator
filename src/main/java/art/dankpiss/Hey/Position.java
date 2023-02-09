package art.dankpiss.Hey;
import org.bukkit.util.BlockVector;
import java.util.HashSet;
import java.util.Set;

// A simple observable
public class Position extends BlockVector {
  private Set<Watcher> watching = new HashSet<>();

  public Position(Watcher me, BlockVector vector) {
    super(vector);
    watching.add(me);
  }

  public void shout(String what) {
    for (Watcher me : watching) {
      me.tell(this, what);
    }
  }

  public void destroy() {
    shout(BlockManager.Action.DESTROY_BLOCK);
  }

  public void onCreate() {}
  public void onDestroy() {}

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Position)) {
      return false;
    }
    Position o = (Position) other;
    return x == o.x && y == o.y && z == o.z;
  }
}