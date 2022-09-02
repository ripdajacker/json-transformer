package dk.mehmedbasic.jsonast.selector;

import dk.mehmedbasic.jsonast.BaseNode;

/**
 * A filter that ands two other filters.
 */
final class AndFilter extends NodeFilter {

  private final NodeFilter left;
  private final NodeFilter right;

  public AndFilter(NodeFilter left, NodeFilter right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public boolean apply(BaseNode node, Integer index) {
    return left.apply(node, index) && right.apply(node, index);
  }
}
