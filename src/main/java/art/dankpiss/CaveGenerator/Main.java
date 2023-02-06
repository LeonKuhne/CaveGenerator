package art.dankpiss.CaveGenerator;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public class Main extends JavaPlugin
{
    private Logger log = Logger.getLogger("CaveGenerator");

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
    {
      return new CaveChunkGenerator(64, this.log);
    }
}
