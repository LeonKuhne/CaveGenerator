package art.dankpiss.CaveGenerator;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import art.dankpiss.Hey.Watcher;
import art.dankpiss.Hey.BlockManager;
import art.dankpiss.Hey.Position;

public class Acid extends Position {
  public Double level;
  public static Double FLOW_LOSS = 1./8.;

  // create source block at location
  public Acid(Watcher watching, BlockVector pos) {
    super(watching, pos);
    initSource();
  }
  public Acid(Watcher watching, Block block) {
    this(watching, block, null);
  }

  public void initSource() {
    this.level = 1.0;
    shout(BlockManager.Action.CREATE_BLOCK);
  }

  @Override
  public void onCreate() {
    Util.at(this).setType(Material.WATER);
  }

  // create reduced block 
  public Acid(Watcher watching, Block block, Acid acid) {
    this(watching, Util.pos(block), acid);
  }
  public Acid(Watcher watching, BlockVector vector, Acid acid) {
    super(watching, vector);
    // create source
    if (acid == null) { initSource(); return; }
    // reduce level
    this.level = acid.level - FLOW_LOSS;
  }
}
