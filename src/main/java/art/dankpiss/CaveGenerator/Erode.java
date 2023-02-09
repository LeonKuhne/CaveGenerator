package art.dankpiss.CaveGenerator;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.BlockVector;
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
    Util.dispatch(this, 1);
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    // verify world is cave world
    Block ice = event.getBlock();
    if (!Util.inCave(ice)) { return; }
    // verify block is packed ice
    if (ice.getType() != Material.PACKED_ICE) { return; }
    // replace with acid
    new Acid(acids, Util.pos(ice));
    acids.cleanup();
  }

  @EventHandler
  public void onWaterFlow(BlockFromToEvent event) {
    // verify world is cave world
    Block water = event.getBlock();
    if (!Util.inCave(water)) { return; }
    // get acid
    // NOTE TODE ACIDS.GET DOESN"T ACTUALLY GET ANYTHING
    Acid acid = acids.get(Util.pos(water));
    // track flowed to block
    followAcid(acid, event.getFace(), event.getToBlock());
    acids.cleanup();
  }

  private void followAcid(Acid fromAcid, BlockFace fromDirection, Block toBlock) {
    // fill acid
    if (fromDirection == BlockFace.UP) { 
      new Acid(acids, Util.pos(toBlock));
    } 
    else {
      new Acid(acids, toBlock, fromAcid);
    }
  }

  @Override
  public void run() {
    Util.log("Acids: " + acids.size());

    // Destroy Mud
    for (Acid acid : acids.values()) {
      // collect all neighboring blocks
      Set<BlockVector> degradables = Util.flow(acid).stream()
        .filter(vector -> {
          Material mat = Util.at(vector).getType();
          return mat == Material.PACKED_MUD || mat == Material.MUD;
        })
        .collect(Collectors.toSet());
      // process as degradables
      degradables.stream()
        .map(vector -> {
          if (degrading.containsKey(vector)) {
            return degrading.get(vector);
          } else {
            return new Degradable(degrading, vector);
          }
        })
        // add damage to block
        .forEach(degraded -> degraded.damage(acid));
      
      // solidy acid
      if (acid.level <= Acid.FLOW_LOSS) {
        acid.destroy();
      }
    }

    // apply changes
    acids.cleanup();
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
    acids.cleanup();
  }

  public void solidifyAll() {
    acids.values().forEach(Acid::destroy);
    acids.cleanup();
  }
}
