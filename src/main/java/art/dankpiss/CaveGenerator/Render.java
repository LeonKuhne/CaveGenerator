package art.dankpiss.CaveGenerator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.util.BlockVector;

public class Render {
  private HashMap<BlockVector, List<Material>> queue;

  // constructor
  public Render() {
    queue = new HashMap<>();
  }

  // queue
  public void queue(Material material, BlockVector vector) {
    System.out.println(String.format(
      "Render.queue: vector is null, {}, {}", material, vector));
    if (queue.containsKey(vector)) {
      queue.get(vector).add(material);
    } else {
      List<Material> list = Arrays.asList(material);
      queue.put(vector, list);
    }
  }

  // draw
  public void draw() {
    for (Entry<BlockVector, List<Material>> entry : queue.entrySet()) {
      BlockVector vector = entry.getKey();
      for (Material material : entry.getValue()) {
        switch (material) {
          case WATER:
          case AIR:
            Util.at(vector).setType(material);
            continue;
          // spawn block falling
          default:
            // put air down first
            Util.at(vector).setType(Material.AIR);
            Util.caveWorld.spawnFallingBlock(vector.toLocation(Util.caveWorld), material.createBlockData());
            continue;
        }
      }
    }
    queue.clear();
  }
}