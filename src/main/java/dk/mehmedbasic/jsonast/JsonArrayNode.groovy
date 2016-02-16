package dk.mehmedbasic.jsonast

import groovy.transform.CompileStatic

/**
 * A JsonArray.
 */
@CompileStatic
class JsonArrayNode extends BaseNode {
    Set<BaseNode> children = new LinkedHashSet<>()

    @Override
    boolean isArray() {
        true
    }


    @Override
    BaseNode get(int index) {
        if (index < children.size()) {
            return children.getAt(index)
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


    @Override
    public String toString() {
        return "JsonArrayNode[$identifier][$children]"
    }
}
