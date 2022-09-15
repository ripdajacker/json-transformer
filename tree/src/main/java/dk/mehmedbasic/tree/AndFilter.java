package dk.mehmedbasic.tree;

/**
 * A filter that ands two other filters.
 */
public final class AndFilter<T> extends NodeFilter<T> {

  private final NodeFilter<T> left;
  private final NodeFilter<T> right;

  public AndFilter(NodeFilter<T> left, NodeFilter<T> right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public boolean test(BaseNode<T> node, Integer index) {
    return left.test(node, index) && right.test(node, index);
  }
}
