package dk.mehmedbasic.jsonast.selector

import dk.mehmedbasic.jsonast.BaseNode
import groovy.transform.PackageScope
import groovy.transform.TypeChecked

/**
 * A filter that ands two other filters.
 */
@TypeChecked
@PackageScope
final class AndFilter extends NodeFilter {
    NodeFilter left
    NodeFilter right

    AndFilter(NodeFilter left, NodeFilter right) {
        this.left = left
        this.right = right
    }

    @Override
    boolean apply(BaseNode node, Integer index) {
        return left.apply(node, index) && right.apply(node, index)
    }
}