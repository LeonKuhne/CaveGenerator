package art.dankpiss.CaveGenerator;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.util.BlockVector;
import art.dankpiss.CaveGenerator.Util.Range;
import art.dankpiss.Hey.Watcher;
import art.dankpiss.Hey.Position;

public class Degradable extends Position<Degradable> {
  private int health;
  private HashMap<Range, Runnable> thresholds;

  public Degradable(Watcher<Degradable> watching, BlockVector pos) {
    super(pos);
    watch(watching, this);
    health = 100;

    // define thresholds using conditionals
    thresholds = new HashMap<>(Map.of(
      // destroy block
      (before, after) -> Util.minThreshold(before, after, 0),
      () -> { Util.at(this).setType(Material.AIR); delete(); },
      // turn to mud
      (before, after) -> Util.minThreshold(before, after, 50),
      () -> Util.at(this).setType(Material.MUD)
    ));
  }

  public void damage(Acid acid) {
    Double speed = 6.0;
    Double delta = (1 - acid.level) * speed;
    // check thresholds
    int before = health;
    health -= delta.intValue();
    thresholds.forEach((range, action) -> {
      if (range.test(before, health)) {
        action.run();
      }
    });
  }
}