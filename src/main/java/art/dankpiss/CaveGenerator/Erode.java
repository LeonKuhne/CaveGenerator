package art.dankpiss.CaveGenerator;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import art.dankpiss.Hey.BlockManager;

public class Erode implements Runnable, Listener {
  public BlockManager<Acid> acids;
  public BlockManager<Degradable> degrading;
  private static final int TICKS_PER_ERODE = 8;
  private double destroyedTarget;

  public Erode() {
    acids = new BlockManager<Acid>();
    degrading = new BlockManager<Degradable>();
    destroyedTarget = 0;
    // water tick rate: 8
    Util.dispatch(this, TICKS_PER_ERODE);
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    // verify world is cave world
    Block ice = event.getBlock();
    if (!Util.inCave(ice)) { return; }
    // verify block is packed ice
    if (ice.getType() != Material.PACKED_ICE) { return; }
    // replace with acid
    new Acid(acids, ice);
    acids.cleanup();
  }

  @EventHandler
  public void onWaterFlow(BlockFromToEvent event) {
    // verify world is cave world
    Block water = event.getBlock();
    if (!Util.inCave(water)) { return; }
    // get acid
    Acid acid = acids.get(Util.key(water));
    Block block = event.getToBlock();
    // fill acid
    if (event.getFace() == BlockFace.UP) { 
      new Acid(acids, block);
    // flow acid
    } else {
      new Acid(acids, block, acid);
    }
    acids.cleanup();
  }

  @Override
  public void run() {
    // destroy mud
    acids.loop(acid -> {
      Util.star(acid).stream()
        // select nearby mud
        .filter(vector -> {
          Material mat = Util.at(vector).getType();
          return mat == Material.PACKED_MUD || mat == Material.MUD;
        })
        // mark degrading
        .map(vector -> {
          String key = Util.key(vector);
          if (degrading.has(key)) {
            return degrading.get(key);
          } else {
            return new Degradable(degrading, vector);
          }
        })
        // damage
        .forEach(degradable -> degradable.etch(acid));
      // solidy acid
      if (destroyedTarget > 0 && acid.level <= Acid.FLOW_LOSS) {
        acid.destroy();
        destroyedTarget--;
      }
    });
    // damage degrading
    destroyedTarget += degrading.loop(degradable -> degradable.damage());
    // expected destruction
    destroyedTarget -= Util.DegradeConfig.destroyed_per_tick * TICKS_PER_ERODE;
  }

  // solidify acid with low water level
  public void solidify() {
    acids.loop(acid -> {
      if (acid.level <= Acid.FLOW_LOSS) {
        acid.destroy();
        destroyedTarget--;
      }
    });
  }

  public void solidifyAll() {
    acids.loop(acid -> acid.destroy());
    destroyedTarget = 0;
  }

  @Override
  public String toString() {
    // show acids, degrading, and destroyed target
    return String.format("Erode: %d acids, %d degrading, %.2f to be destroyed",
      acids.size(), degrading.size(), destroyedTarget);
  }
}
