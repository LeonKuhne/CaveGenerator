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

public class Erode implements Runnable, Listener {
  private Set<Acid> acids;

  public Erode() {
    acids = new HashSet<>();
    // start erosion (same interval as water flow)
    Util.dispatch(this, 8);
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    Util.log("Block placed");
    // verify world is cave world
    Block ice = event.getBlock();
    if (!Util.inCave(ice)) { return; }
    // verify block is packed ice
    if (ice.getType() != Material.PACKED_ICE) { return; }
    // replace with acid
    ice.setType(Material.WATER);
    BlockVector pos = Util.pos(ice);
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
    Block flowedTo = event.getToBlock();
    // determine new level
    BlockVector pos = Util.pos(flowedTo);
    if (event.getFace() == BlockFace.DOWN) {
      acid = new Acid(pos);
    } else {
      acid = new Acid(pos, acid.level - 1/8);
    }
    acids.add(acid);
  }

  @Override
  public void run() {
    // walk starting from the source block down all of the water streams
    // or figure something better out :/
    Set<Acid> newAcids = acids.stream()
      .map(acid -> walk(acid))
      .flatMap(Set::stream)
      .collect(Collectors.toSet());
    // add new acids
    acids.addAll(newAcids);

    // show acid count
    Util.log("Acids: " + acids.size());
  }

  // starting at an acid block
  // return a list of all neighboring water blocks
  public Set<Acid> walk(Acid acid) {
    // lower water level
    Double newLevel = acid.level - 1/8;
    if (newLevel < 0) { return new HashSet<>(); }
    // select neighboring water blocks
    Set<Acid> list = Util.flow(acid)
      .stream()
      .filter(v -> Util.get(v).getType() == Material.AIR)
      .filter(v -> !acids.contains(v))
      .map(v -> new Acid(v, newLevel))
      .collect(Collectors.toSet());
    return list;
  }
}
