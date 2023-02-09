package art.dankpiss.CaveGenerator;
import org.bukkit.util.BlockVector;

public class Acid extends BlockVector {
  public Double level;

  public Acid(BlockVector pos, Double waterLevel) {
    super(pos);
    this.level = waterLevel;
  }

  // source block
  public Acid(BlockVector pos) {
    this(pos, 1.0);
  }
}
