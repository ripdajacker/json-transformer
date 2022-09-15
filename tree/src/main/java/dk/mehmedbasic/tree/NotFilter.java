package dk.mehmedbasic.tree;

/**
 * A not filter
 */
public class NotFilter<T> extends NodeFilter<T> {

  private final NodeFilter<T> delegate;

  public NotFilter(NodeFilter<T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public boolean test(BaseNode<T> node, Integer index) {
    return !delegate.test(node, index);
  }
}
