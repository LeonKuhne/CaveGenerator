package art.dankpiss.CaveGenerator;
import org.bukkit.util.BlockVector;

public class Acid extends BlockVector {
  private Double waterLevel;

  public Acid(BlockVector pos, Double waterLevel) {
    super(pos);
    this.waterLevel = waterLevel;
  }

  // source block
  public Acid(BlockVector pos) {
    this(pos, 1.0);
  }
}
