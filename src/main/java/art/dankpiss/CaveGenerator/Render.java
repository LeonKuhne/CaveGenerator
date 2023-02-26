package art.dankpiss.CaveGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.util.BlockVector;

public class Render {
  private HashMap<BlockVector, ArrayList<Material>> queue;

  // constructor
  public Render() {
    queue = new HashMap<>();
  }

  // queue
  public void queue(Material material, BlockVector vector) {
    ArrayList<Material> list = queue.getOrDefault(vector, new ArrayList<>());
    list.add(material);
    queue.put(vector, list);
  }

  // draw
  // returns number of failed constructed blocks
  public int draw() {
    int failed = 0;
    for (Entry<BlockVector, ArrayList<Material>> entry : queue.entrySet()) {
      BlockVector vector = entry.getKey();
      for (Material material : entry.getValue()) {
        switch (material) {
          // spawn block falling
          case MUD:
          case PACKED_MUD:
            if (!Util.placeFalling(vector, material)) {
              failed++;
            };
            break;
          // place block
          case WATER:
          case AIR:
          default:
            Util.at(vector).setType(material);
            break;
        }
      }
    }
    queue.clear();
    return failed;
  }
}