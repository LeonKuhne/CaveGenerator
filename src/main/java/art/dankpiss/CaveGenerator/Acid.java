package art.dankpiss.CaveGenerator;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import art.dankpiss.Hey.Watcher;
import art.dankpiss.Hey.Position;

public class Acid extends Position<Acid> {
  public Double level;
  public static Double FLOW_LOSS = 1./8.;

  // create source block at location
  public Acid(Watcher<Acid> watching, BlockVector pos) {
    super(pos);
    watch(watching, this);
    initSource();
  }
  public Acid(Watcher<Acid> watching, Block block) {
    this(watching, block, null);
  }

  public void initSource() {
    this.level = 1.0;
    Util.at(this).setType(Material.WATER);
    create();
  }

  public void destroy() {
    Util.at(this).setType(Material.MUD);
    delete();
  }

  // create reduced block 
  public Acid(Watcher<Acid> watching, Block block, Acid acid) {
    this(watching, Util.pos(block), acid);
  }
  public Acid(Watcher<Acid> watching, BlockVector vector, Acid acid) {
    super(vector);
    watch(watching, this);
    // create source
    if (acid == null) { initSource(); return; }
    // reduce level
    this.level = acid.level - FLOW_LOSS;
  }
}
