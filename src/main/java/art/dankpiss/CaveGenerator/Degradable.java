package art.dankpiss.CaveGenerator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.util.BlockVector;
import art.dankpiss.CaveGenerator.Util.Range;
import art.dankpiss.Hey.BlockManager;
import art.dankpiss.Hey.Position;

public class Degradable extends Position<Degradable> {
  public Double health;
  private HashMap<Range, Runnable> thresholds;
  private Set<Acid> etchers;

  public Degradable(BlockManager<Degradable> manager, BlockVector pos) {
    super(pos);
    if (manager.has(this)) { return; }
    watch(manager, this);
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
    Double distanceFactor = 1. / this.distance(acid);
    Double directionFactor = acid.getBlockY() > getBlockY() 
      ? Util.DegradeConfig.down_likeliness : 1;
    Double delta 
      = (Util.DegradeConfig.level_boundary - acid.level) 
      * Util.DegradeConfig.speed
      * directionFactor
      * distanceFactor
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