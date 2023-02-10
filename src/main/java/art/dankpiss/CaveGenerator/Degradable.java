package art.dankpiss.CaveGenerator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.util.BlockVector;
import art.dankpiss.CaveGenerator.Util.Range;
import art.dankpiss.Hey.Watcher;
import art.dankpiss.Hey.Position;

public class Degradable extends Position<Degradable> {
  public Double health;
  private HashMap<Range, Runnable> thresholds;
  private Set<Acid> etchers;

  public Degradable(Watcher<Degradable> watching, BlockVector pos) {
    super(pos);
    watch(watching, this);
    etchers = new HashSet<>();
    // adjust health
    if (Util.at(this).getType() == Material.MUD) {
      health = 50.;
    } else {
      health = 100.;
    }

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

  public void etch(Acid acid) {
    etchers.add(acid);
  }

  public void damage() {
    for (Acid acid : etchers) {
      damage(acid);
    }
    // cleanup
    etchers.clear();
  }

  private void damage(Acid acid) {
    Double delta 
      = (1 - acid.level) 
      * Util.DEGRADE_SPEED
      * (getBlockY() < acid.getBlockY() ? 1 : 0.5)
      * Math.random();
    // check thresholds
    double before = health;
    health -= delta;
    thresholds.forEach((range, action) -> {
      if (range.test(before, health)) {
        action.run();
      }
    });
  }

}