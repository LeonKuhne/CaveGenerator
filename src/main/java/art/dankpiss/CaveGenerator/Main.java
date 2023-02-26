package art.dankpiss.CaveGenerator;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockVector;

import art.dankpiss.CaveGenerator.Util.DegradeConfig;

import static art.dankpiss.CaveGenerator.Util.Color.*;

public class Main extends JavaPlugin
{
  public static CommandBuilder setCommands;

  public static CommandBuilder commands = new CommandBuilder()
  .add("state", args -> Util.log(Util.erosion.toString()))
  .add("solidify", args -> Util.erosion.solidify())
  .add("target", args -> Util.erosion.setTarget(Float.valueOf(args.get(0))))
  .add("reset", args -> {
    Util.erosion.reset();
    Util.log(Util.erosion.toString());
  })
  .add("health", args -> {
    Util.erosion.degrading.tap(degraded
      -> Util.log(degraded.toString() + ": " + degraded.health));
  })
  .add("level", args -> {
    Util.erosion.acids.tap(acid
      -> Util.log(acid.toString() + ": " + acid.level));
  })
  .add("set", args -> {
    if (args.size() < 2) { 
      for (Field field : DegradeConfig.class.getFields()) {
        String name = field.getName();
        try {
          Util.log(name + ": " + field.get(DegradeConfig.class));
        } catch (Exception e) {
          Util.log("error getting " + name);
        }
      }
    }
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
    setCommands = new CommandBuilder();
    for (Field field : DegradeConfig.class.getFields()) {
      String name = field.getName();
      setCommands.add(name, args -> {
        try {
          field.set(DegradeConfig.class, Double.parseDouble(args.get(0)));
        } catch (Exception e) {
          Util.log("error setting " + name);
        }
      });
    }

    Util.enable(this);
    // start erosion
    new Acid(Util.erosion.acids, new BlockVector(6, 255, 3));
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
        sender.sendMessage(RED + "Usage: /erode\n" + commands.toString() + " [args]");
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
