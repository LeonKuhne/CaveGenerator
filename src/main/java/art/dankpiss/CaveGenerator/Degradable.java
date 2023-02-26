package art.dankpiss.CaveGenerator;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.util.BlockVector;
import art.dankpiss.CaveGenerator.Util.Range;
import art.dankpiss.Hey.BlockManager;
import art.dankpiss.Hey.Position;

public class Degradable extends Position<Degradable> {
  public Double health; // 0-100 degredation state
  // NOTE enhancement: friction could be 6 dimentional: one per face
  private Double friction; // 0-1 damage multiplier
  private HashMap<Range, Runnable> thresholds;
  private Double damageQueue;
  private boolean permiable;

  public Degradable(BlockManager<Degradable> manager, BlockVector pos) {
    super(pos);
    watch(manager, this);
    // adjust health
    if (Util.at(this).getType() == Material.MUD) {
      health = 50.;
    } else {
      health = 100.;
    }
    friction = 1.;
    damageQueue = 0.;
    permiable = true;

    // define thresholds using conditionals
    thresholds = new HashMap<>(Map.of(
      // destroy block
      (before, after) -> Util.crossThresholdDown(before, after, 0),
      () -> { Util.render.queue(Material.AIR, pos); delete(); },
      // turn to packed mud
      (before, after) -> Util.crossThresholdDown(before, after, 50),
      () -> Util.render.queue(Material.MUD, pos),
      // turn to packed mud
      (before, after) -> Util.crossThresholdUp(before, after, 50),
      () -> Util.render.queue(Material.PACKED_MUD, pos)
    ));
  }

  public void damage(Acid acid) {
    if (!permiable) { return; }
    double distance = this.distance(acid);
    double directionFactor = acid.getBlockY() > getBlockY() 
      ? Util.DegradeConfig.down_likeliness : 1;
    double acidity = Util.DegradeConfig.level_boundary - acid.level;
    double friction = Math.random() * Util.DegradeConfig.randomness;
    // apply damage
    double delta 
      = acidity
      * Util.DegradeConfig.damage
      * 1. / distance 
      * directionFactor 
      * (1. - friction);
    // queue up damage
    damageQueue += delta;
    // reduce friction
    if (distance == 1) {
      friction -= friction * Util.DegradeConfig.friction_damage; 
    }
  }

  // apply the damage
  public void applyDamage() {
    if (!permiable) { return; }
    double before = health;
    // apply damage, cap health at 1000
    health -= damageQueue * friction;
    health = Math.min(health, 1000.);
    // update permiability
    if (friction <= 0) { 
      permiable = false;
      Util.render.queue(Material.GLOWSTONE, this.clone());
    }
    // check if thresholds breached
    thresholds.forEach((range, action) -> {
      if (range.test(before, health)) {
        action.run();
        return;
      }
    });
  }
}
