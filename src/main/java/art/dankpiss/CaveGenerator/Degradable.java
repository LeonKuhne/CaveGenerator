package art.dankpiss.CaveGenerator;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.util.BlockVector;
import art.dankpiss.CaveGenerator.Util.Range;
import art.dankpiss.Hey.BlockManager;
import art.dankpiss.Hey.Position;

public class Degradable extends Position<Degradable> {
  public Double health;
  private HashMap<Range, Runnable> thresholds;
  private Double damageQueue;

  public Degradable(BlockManager<Degradable> manager, BlockVector pos) {
    super(pos);
    if (manager.has(this)) { return; }
    watch(manager, this);
    // adjust health
    if (Util.at(this).getType() == Material.MUD) {
      health = 50.;
    } else {
      health = 100.;
    }
    damageQueue = 0.;

    // define thresholds using conditionals
    thresholds = new HashMap<>(Map.of(
      // destroy block
      (before, after) -> Util.crossThresholdDown(before, after, 0),
      () -> { Util.at(this).setType(Material.AIR); delete(); },
      // turn to packed mud
      (before, after) -> Util.crossThresholdDown(before, after, 50),
      () -> Util.at(this).setType(Material.MUD),
      // turn to packed mud
      (before, after) -> Util.crossThresholdUp(before, after, 50),
      () -> Util.at(this).setType(Material.PACKED_MUD)
    ));
  }

  public void damage(Acid acid) {
    double distanceFactor = 1. / this.distance(acid);
    double directionFactor = acid.getBlockY() > getBlockY() 
      ? Util.DegradeConfig.down_likeliness : 1;
    double delta 
      = (acid.level - Util.DegradeConfig.level_boundary) 
      * Util.DegradeConfig.speed
      * directionFactor
      * distanceFactor
      * Math.random();
    // check thresholds
    double before = health;
    double after = health - delta;
    thresholds.forEach((range, action) -> {
      if (range.test(before, after)) {
        action.run();
      }
    });
    // queue up damage
    damageQueue -= delta;
  }

  // apply the damage
  public void applyDamage() {
    health += damageQueue;
  }
}