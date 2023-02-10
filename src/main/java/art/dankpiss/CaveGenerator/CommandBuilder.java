package art.dankpiss.CaveGenerator;
import java.util.HashMap;
import java.util.List;
import art.dankpiss.CaveGenerator.Util.Callback;

public class CommandBuilder extends HashMap<String, Callback<List<String>>>
{

  public CommandBuilder add(String command, Callback<List<String>> callback) {
    this.put(command, callback);
    return this;
  }

  // returns null if successful, otherwise error message
  public String call(List<String> args) {
    // parse command from first arg
    if (args.size() < 0) {
      return "No command provided";
    }
    String command = args.remove(0);
    if (!this.containsKey(command)) {
      return "Command not found: " + command + "\n" +
        "Available commands: " + toString();
    }
    try {
      get(command).run(args); 
      return null;
    } catch (Exception e) {
      return "Error: " + e.getMessage();
    }
  }

  @Override
  public String toString() {
    return String.join("|", keySet());
  }
}
