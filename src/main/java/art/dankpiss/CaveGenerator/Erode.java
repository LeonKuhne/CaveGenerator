package art.dankpiss.CaveGenerator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.BlockVector;

public class Erode implements Runnable, Listener {
  private World world;
  private Set<Acid> acids;

  public Erode(World world) {
    this.world = world;
    acids = new HashSet<>();
    // start erosion (same interval as water flow)
    Util.dispatch(this, 8);
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    // verify world is cave world
    Block ice = event.getBlock();
    if (ice.getWorld() != world) { return; }
    // verify block is packed ice
    if (ice.getType() != Material.PACKED_ICE) { return; }
    // replace with acid
    ice.setType(Material.WATER);
    BlockVector pos = Util.pos(ice);
    acids.add(new Acid(pos));
  }

  @Override
  public void run() {
    // walk starting from the source block down all of the water streams
    // or figure something better out :/
  }
}
