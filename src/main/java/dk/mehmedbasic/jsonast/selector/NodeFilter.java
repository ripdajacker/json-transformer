package dk.mehmedbasic.jsonast.selector;

import dk.mehmedbasic.jsonast.BaseNode;

/**
 * A node filter that can filter a subtree.
 */
public abstract class NodeFilter {

  /**
   * Whether or not the filter accepts the node.
   *
   * @param node the node in question.
   * @return true if accepted, false otherwise.
   */
  public abstract boolean apply(BaseNode node, Integer index);

  /**
   * Ands the filter with the given filter.
   *
   * @param that the filter.
   * @return a and filter of this and that.
   */
  public NodeFilter and(NodeFilter that) {
    return new AndFilter(this, that);
  }

  /**
   * Creates a not filter of this.
   *
   * @return the not filter.
   */
  public NodeFilter not() {
    return new NotFilter(this);
  }
}
