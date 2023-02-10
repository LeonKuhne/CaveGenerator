package art.dankpiss.CaveGenerator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockVector;
import static art.dankpiss.CaveGenerator.Util.Color.*;

public class Main extends JavaPlugin
{
  public static CommandBuilder setCommands = new CommandBuilder()
  .add("speed", args -> Util.DegradeConfig.speed = Double.parseDouble(args.get(0)))
  .add("sink-speed", args -> Util.DegradeConfig.down_likeliness = Double.parseDouble(args.get(0)))
  .add("destoy-per-tick", args -> Util.DegradeConfig.destroyed_per_tick = Double.parseDouble(args.get(0)));

  public static CommandBuilder commands = new CommandBuilder()
  .add("state", args -> Util.log(Util.erosion.toString()))
  .add("solidify", args -> Util.erosion.solidify())
  .add("solidify-all", args -> Util.erosion.solidifyAll())
  .add("health", args -> {
    Util.erosion.degrading.tap(degraded
      -> Util.log(degraded.toString() + ": " + degraded.health));
  })
  .add("level", args -> {
    Util.erosion.acids.tap(acid
      -> Util.log(acid.toString() + ": " + acid.level));
  })
  .add("settings", args -> {
    Util.log("speed: " + Util.DegradeConfig.speed);
    Util.log("sink-speed: " + Util.DegradeConfig.down_likeliness);
    Util.log("destoy-per-tick: " + Util.DegradeConfig.destroyed_per_tick);
  })
  .add("set", args -> {
    if (args.size() < 2) { Util.log(setCommands.toString()); return; }
    setCommands.call(args);
  });

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
    // start erosion
    new Acid(Util.erosion.acids, new BlockVector(0, 255, 0));
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
  public boolean onCommand(CommandSender sender, Command command, String label, String[] strArgs) {
    if (command.getName().toLowerCase().equals("erode")) {
      if (strArgs.length == 0 || strArgs[0] == "help") {
        sender.sendMessage(RED + "Usage: /erode " + commands.toString() + " [args]");
        return true;
       }
      // convert args to list
      String err = commands.call(new ArrayList<String>(Arrays.asList(strArgs)));
      if (err != null) { sender.sendMessage(RED + err); }
      return true;
    }
    return false;
  }
}
