package dk.mehmedbasic.jsonast

import groovy.transform.CompileStatic

/**
 * A Json object node.
 */
@CompileStatic
class JsonObjectNode extends BaseNode {
    LinkedList<BaseNode> children = new LinkedList<>()

    JsonObjectNode() {
    }

    @Override
    BaseNode get(String name) {
        children.find { it.identifier.name == name }
    }

    @Override
    void addChild(BaseNode node) {
        super.addChild(node)
        children << node
    }

    @Override
    boolean isObject() {
        true
    }

    @Override
    boolean isArray() {
        false
    }

    int getLength() {
        children.size()
    }

    @Override
    void removeNode(BaseNode node) {
        if (children.contains(node)) {
            children.remove(node)
            super.removeNode(node)
            node.parent = null
        }
    }


    @Override
    public String toString() {
        return "JsonObjectNode[$identifier]{$children}";
    }
}
