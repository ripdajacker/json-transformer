package dk.mehmedbasic.jsonast.selector;

import dk.mehmedbasic.jsonast.BaseNode;

/**
 * A not filter
 */
class NotFilter extends NodeFilter {

  public NotFilter(NodeFilter delegate) {
    this.delegate = delegate;
  }

  @Override
  public boolean apply(BaseNode node, Integer index) {
    return !delegate.apply(node, index);
  }

  private final NodeFilter delegate;
}
