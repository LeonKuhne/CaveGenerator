package art.dankpiss.CaveGenerator;
import org.bukkit.util.BlockVector;

import art.dankpiss.Hey.BlockManager;
import art.dankpiss.Hey.Watcher;
import art.dankpiss.Hey.Position;

public class Degradable extends Position {
  private int health;
  public static class Action {
    // turn packed mud under 50% health into mud
    public static String DEGRADE_BLOCK = "degrade"; // 50%
    // turn mud under 25% health into liquid block
    public static String SINK_BLOCK    = "sink";    // 25%
  }

  public Degradable(Watcher watching, BlockVector pos) {
    super(watching, pos);
    health = 100;
  }

  public void damage() {
    health -= 25;
    if (health <= 0) {
      shout(BlockManager.Action.DESTROY_BLOCK);
    } else if (health <= 25) {
      shout(Action.SINK_BLOCK);
    } else if (health <= 50) {
      shout(Action.DEGRADE_BLOCK);
    }
  }
}