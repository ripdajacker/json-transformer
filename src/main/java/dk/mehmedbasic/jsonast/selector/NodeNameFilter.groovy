package dk.mehmedbasic.jsonast.selector

import dk.mehmedbasic.jsonast.JsonIdentifier
import groovy.transform.TypeChecked

/**
 * Node name filter.
 */
@TypeChecked
class NodeNameFilter extends JsonIdentifierFilter {
    private String nodeName;

    NodeNameFilter(String nodeName) {
        this.nodeName = nodeName
    }

    @Override
    boolean apply(JsonIdentifier identifier) {
        return identifier.name == nodeName
    }
}
