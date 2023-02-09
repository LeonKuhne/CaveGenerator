package art.dankpiss.CaveGenerator;
import java.util.HashSet;
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
    new Acid(acids, Util.pos(ice));
  }

  @EventHandler
  public void onWaterFlow(BlockFromToEvent event) {
    // verify world is cave world
    Block water = event.getBlock();
    if (!Util.inCave(water)) { return; }
    // get acid
    Acid acid = acids.get(Util.pos(water));
    // track flowed to block
    followAcid(acid, event.getFace(), event.getToBlock());
  }

  private void followAcid(Acid fromAcid, BlockFace fromDirection, Block toBlock) {
    if (fromDirection == BlockFace.UP) { fillAcid(toBlock); } 
    else { expandAcid(toBlock, fromAcid); }
  }

  // expand full block
  private void fillAcid(Block block) {
    new Acid(acids, Util.pos(block));
  }

  // expand reducing level
  private void expandAcid(Block block, Acid acid) {
    new Acid(acids, block, acid);
  }

  @Override
  public void run() {
    Util.log("Acids: " + acids.size());

    // Destroy Mud
    Set<BlockVector> caveinables = new HashSet<>();
    for (Acid acid : acids.values()) {
      // collect all neighboring blocks
      caveinables.addAll(
        Util.flow(acid).stream()
          .filter(vector -> {
            Material mat = Util.at(vector).getType();
            return mat == Material.PACKED_MUD || mat == Material.MUD;
          })
          .collect(Collectors.toSet()));
    }
    Util.log("Caveinables: " + caveinables.size());

    // Damage Mud
    for (BlockVector pos : caveinables) {
      Degradable degraded;
      // find/create degradable
      if (degrading.containsKey(pos)) {
        degraded = degrading.get(pos);
      } else {
        degraded = new Degradable(degrading, pos);
      }
      // add damage to block
      degraded.damage();
    }

    // destroy surrounding mud if acid is falling
    // solidify acid
  }

  public void solidify(Acid acid) {
    acid.destroy();
    Util.at(acid).setType(Material.MUD);
  }
  
  // solidify acid with low water level
  public void solidify() {
    Util.loop(acids.values(), acid -> {
      if (acid.level <= Acid.FLOW_LOSS) {
        solidify(acid);
      }
    });
  }

  public void solidifyAll() {
    acids.values().forEach(this::solidify);
  }
}
