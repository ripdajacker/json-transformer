package dk.mehmedbasic.tree;

/**
 * A node filter that can filter a subtree.
 */
public abstract class NodeFilter<T> {

  /**
   * Test whether the filter accepts the node.
   *
   * @param node the node in question.
   * @return true if accepted, false otherwise.
   */
  public abstract boolean test(BaseNode<T> node, Integer index);

  /**
   * Ands the filter with the given filter.
   *
   * @param that the filter.
   * @return a and filter of this and that.
   */
  public NodeFilter<T> and(NodeFilter<T> that) {
    return new AndFilter<>(this, that);
  }

  /**
   * Creates a not filter of this.
   *
   * @return the not filter.
   */
  public NodeFilter<T> not() {
    return new NotFilter<>(this);
  }
}
