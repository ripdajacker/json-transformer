package dk.mehmedbasic.jsonast

import com.google.common.collect.Iterables
import groovy.transform.TypeChecked

/**
 * A JsonArray.
 */
@TypeChecked
class JsonArrayNode extends BaseNode {
    Set<BaseNode> children = new LinkedHashSet<>()

    @Override
    boolean isArray() {
        true
    }


    @Override
    BaseNode get(int index) {
        if (index < children.size()) {
            return Iterables.get(children, index)
        }
        null
    }

    @Override
    void addChild(BaseNode node) {
        super.addChild(node)
        children.add(node)
    }

    @Override
    void removeNode(BaseNode node) {
        if (children.contains(node)) {
            children.remove(node)
            super.removeNode(node)
        }
    }

    int getLength() {
        return children.size()
    }
}
