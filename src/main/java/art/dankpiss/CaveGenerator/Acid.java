package art.dankpiss.CaveGenerator;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

public class Acid extends BlockVector {
  public Double level;

  // source block
  public Acid(Block block) {
    this(block, null);
  }

  public Acid(Block block, Acid acid) {
    super(Util.pos(block));
    this.level = acid.level - 1/8;
  }

  public Acid(BlockVector pos) {
    super(pos);
    this.level = 1.0;
  }
}
