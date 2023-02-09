package art.dankpiss.CaveGenerator;
import org.bukkit.Material;
import org.bukkit.util.BlockVector;
import art.dankpiss.Hey.BlockManager;
import art.dankpiss.Hey.Watcher;
import art.dankpiss.Hey.Position;

public class Degradable extends Position {
  private int health;

  public Degradable(Watcher watching, BlockVector pos) {
    super(watching, pos);
    health = 100;
  }

  public void damage(Acid acid) {
    Double delta = - 1.0 / acid.level - 0.05;
    // adjust texture
    if (Util.minThreshold(0, health, delta)) {
      shout(BlockManager.Action.DESTROY_BLOCK);
    } else if (Util.minThreshold(25, health, delta)) {
      // do nothing
    } else if (Util.minThreshold(50, health, delta)) {
      // turn block to mud
      Util.at(this).setType(Material.MUD);
    }

    // apply delta
    health += delta;
  }
}