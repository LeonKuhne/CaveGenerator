package art.dankpiss.CaveGenerator;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.BlockVector;

public class Erode implements Runnable, Listener {
  private Set<Acid> acids;

  public Erode() {
    acids = new HashSet<>();
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
    placeAcid(Util.pos(ice));
  }

  public void placeAcid(BlockVector pos) {
    Util.at(pos).setType(Material.WATER);
    acids.add(new Acid(pos));
  }

  @EventHandler
  public void onWaterFlow(BlockFromToEvent event) {
    Util.log("Water flowed");
    // verify world is cave world
    Block water = event.getBlock();
    if (!Util.inCave(water)) { return; }
    // get acid
    Acid acid = acids.stream()
      .filter(x -> x.equals(Util.pos(water)))
      .findFirst().orElse(null);
    // track flowed to block
    flow(acid, event.getFace(), event.getToBlock());
  }

  private void flow(Acid fromAcid, BlockFace inDirection, Block toBlock) {
    if (inDirection == BlockFace.DOWN) { expand(toBlock); } 
    else { expand(toBlock, fromAcid); }
  }

  // expand acid full block
  private void expand(Block block) {
    acids.add(new Acid(block));
  }

  // expand acid recucing level
  private void expand(Block block, Acid acid) {
    acids.add(new Acid(block, acid));
  }

  @Override
  public void run() {
    // show acid count
    Util.log("Acids: " + acids.size());
  }

  public void solidify() {
    // solidify acid with low water level
    Set<Acid> solidified = new HashSet<>();
    for (Acid acid : acids) {
      Util.log("Acid level: " + acid.level);
      if (acid.level <= 1/8) {
        solidified.add(acid);
        Util.at(acid).setType(Material.MUD);
      }
    }
    // apply solidification
    acids.removeAll(solidified);
  }

  public void solidifyAll() {
    // solidify all acid
    for (Acid acid : acids) {
      Util.at(acid).setType(Material.MUD);
    }
    acids = new HashSet<>();
  }
}
