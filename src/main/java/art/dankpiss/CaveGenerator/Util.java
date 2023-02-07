package art.dankpiss.CaveGenerator;

import java.util.logging.Logger;

public class Util {

  public static final int SEGMENTS = 0;
  private static final Logger logger = Logger.getLogger(
    Colors.BG_RED + Colors.BLACK + "> " + Colors.WHITE + "CAVE WARS"
    + Colors.BLACK + " <" + Colors.BG_RESET + Colors.RESET
  );

  public class Colors { // auto-generated
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLACK = "\u001B[30m";
    public static final String WHITE = "\u001B[37m";
    public static final String STEEL = "\u001B[38;5;8m";
    // custom color, red background, black red text
    public static final String BG_RESET = "\u001B[49;39m";
    public static final String BG_RED = "\u001B[48;5;88m";
  }

  static void log(String msg) { 
    logger.info(Colors.STEEL + msg);
  }
}
