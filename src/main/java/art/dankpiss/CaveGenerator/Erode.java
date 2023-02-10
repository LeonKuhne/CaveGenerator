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
  //private int destroyed;

  public Erode() {
    acids = new BlockManager<Acid>();
    degrading = new BlockManager<Degradable>();
    //destroyed = 0;
    // start erosion on same tick rate as water
    Util.dispatch(this, 8);
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
  }

  @EventHandler
  public void onWaterFlow(BlockFromToEvent event) {
    // verify world is cave world
    Block water = event.getBlock();
    if (!Util.inCave(water)) { return; }
    // get acid
    Acid acid = acids.get(Util.pos(water));
    Block block = event.getToBlock();
    // fill acid
    if (event.getFace() == BlockFace.UP) { 
      new Acid(acids, block);
    // flow acid
    } else {
      new Acid(acids, block, acid);
    }
  }

  @Override
  public void run() {
    Util.log("Acids: " + acids.size());
    Util.log("Degrading: " + degrading.size());
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
          if (degrading.containsKey(vector)) {
            return degrading.get(vector);
          } else {
            return new Degradable(degrading, vector);
          }
        })
        // deal damage 
        .forEach(degraded -> degraded.damage(acid));
      // solidy acid
      if (acid.level <= Acid.FLOW_LOSS) {
        acid.destroy();
      }
    });
    // cleanup changes
    degrading.cleanup();
  }

  // solidify acid with low water level
  public void solidify() {
    for (Acid acid : acids.values()) {
      Util.log("Acid Level: " + acid.level);
      if (acid.level <= Acid.FLOW_LOSS) {
        acid.destroy();
      }
    }
  }

  public void solidifyAll() {
    acids.values().forEach(Acid::destroy);
  }
}
