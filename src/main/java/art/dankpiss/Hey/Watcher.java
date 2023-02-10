package art.dankpiss.Hey;

// A simple observer
public interface Watcher<T> {
  public void create(T elem);
  public void delete(T elem);
}
