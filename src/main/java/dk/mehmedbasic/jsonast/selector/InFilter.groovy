package dk.mehmedbasic.jsonast.selector

import dk.mehmedbasic.jsonast.BaseNode
import groovy.transform.TypeChecked

/**
 * A filter for checking if a node is in a collection.
 */
@TypeChecked
class InFilter extends NodeFilter {
    Collection<BaseNode> nodes

    InFilter(Collection<BaseNode> nodes) {
        this.nodes = nodes
    }

    @Override
    boolean apply(BaseNode node, Integer index) {
        return nodes.contains(node)
    }
}
