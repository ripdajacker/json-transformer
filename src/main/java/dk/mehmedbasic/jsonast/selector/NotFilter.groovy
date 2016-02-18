package dk.mehmedbasic.jsonast.selector

import dk.mehmedbasic.jsonast.BaseNode
import groovy.transform.PackageScope
import groovy.transform.TypeChecked

/**
 * A not filter
 */
@TypeChecked
@PackageScope
class NotFilter extends NodeFilter {
    private NodeFilter delegate

    NotFilter(NodeFilter delegate) {
        this.delegate = delegate
    }

    @Override
    boolean apply(BaseNode node, Integer index) {
        return !delegate.apply(node, index)
    }
}
