package dk.mehmedbasic.jsonast.selector

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonIdentifier
import groovy.transform.PackageScope
import groovy.transform.TypeChecked

/**
 * A filter that works on the identifier of the node.
 */
@TypeChecked
@PackageScope
abstract class JsonIdentifierFilter extends NodeFilter {
    @Override
    boolean apply(BaseNode node, Integer index) {
        return apply(node.identifier)
    }

    abstract boolean apply(JsonIdentifier identifier)

}
