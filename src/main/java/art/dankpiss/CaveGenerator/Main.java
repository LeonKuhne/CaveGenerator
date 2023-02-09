package art.dankpiss.CaveGenerator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import static art.dankpiss.CaveGenerator.Util.Color.*;

public class Main extends JavaPlugin
{
  @Override
  public void onLoad() {
    File worldFolder = new File("caves");
    if (!worldFolder.exists()) { return; }

    // not sure this matters
    getServer().unloadWorld("caves", false);

    // force delete world, walk through files
    try {
      Files.walk(worldFolder.toPath())
        .map(path -> path.toFile())
        .forEach(File::delete);
      Util.log(GREEN + "Deleted caves");
    } catch (IOException e) {
      Util.log(RED + "Failed removing caves ");
      e.printStackTrace();
    }
  }

  @Override
  public void onEnable() {
    Util.enable(this);
  }

  @Override
  public void onDisable() {
    Util.disable();
  }

  @Override
  public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
    int nSegments = Util.SEGMENTS;
    // optionally parse id to segment count
    if (id != null && !id.isEmpty()) {
      try { nSegments = Integer.parseInt(id); }
      catch (NumberFormatException e) {
        Util.log(RED + "Invalid segment count: " + id);
      }
    }
    return new CaveChunkGenerator(nSegments);
  }

  // add a command to solidify acid with low level
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equals("solidify")) {
      // all if arg provided
      if (args.length > 0) {
        Util.erosion.solidifyAll();
        return true;
      } else {
        Util.erosion.solidify();
      }
      return true;
    }
    return false;
  }
}
