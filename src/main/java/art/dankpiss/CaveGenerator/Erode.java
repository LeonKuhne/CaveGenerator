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
  private int destroyed;

  public Erode() {
    acids = new BlockManager<Acid>();
    degrading = new BlockManager<Degradable>();
    destroyed = -5; // starting buffer
    // water tick rate: 8
    Util.dispatch(this, 32);
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
    Acid acid = acids.get(water);
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
    Util.log("Acids: " + acids.size());
    Util.log("Degrading: " + degrading.size());
    Util.log("Destroyed: " + destroyed);
    // destroy mud
    acids.loop(acid -> {
      Util.flow(acid).stream()
        // select nearby mud
        .filter(vector -> {
          Material mat = Util.at(vector).getType();
          return mat == Material.PACKED_MUD || mat == Material.MUD;
        })
        // mark degrading
        .map(vector -> {
          if (degrading.has(vector)) {
            return degrading.get(vector);
          } else {
            return new Degradable(degrading, vector);
          }
        })
        // damage
        .forEach(degradable -> degradable.etch(acid));
      // solidy acid
      if (destroyed > 0 && acid.level <= Acid.FLOW_LOSS) {
        acid.destroy();
        destroyed--;
      }
    });
    degrading.values().forEach(degradable -> degradable.damage());
    // cleanup changes
    destroyed += degrading.cleanup();
  }

  // solidify acid with low water level
  public void solidify() {
    // print out acid keys
    Util.log("Acids keys: " + acids.keySet());
    for (Acid acid : acids.values()) {
      Util.log("Acid Level: " + acid.level);
      if (acid.level <= Acid.FLOW_LOSS) {
        acid.destroy();
        destroyed--;
      }
    }
  }

  public void solidifyAll() {
    acids.values().forEach(Acid::destroy);
    destroyed -= acids.size();
  }
}
