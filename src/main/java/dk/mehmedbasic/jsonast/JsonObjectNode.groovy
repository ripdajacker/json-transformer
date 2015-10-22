package dk.mehmedbasic.jsonast

import groovy.transform.TypeChecked

/**
 * A Json object node.
 */
@TypeChecked
class JsonObjectNode extends BaseNode {
    List<BaseNode> children = new LinkedList<>()

    private Map<String, BaseNode> nameToChildMap = [:]

    private void updateMap() {
        nameToChildMap.clear()

        for (BaseNode node : children) {
            nameToChildMap.put(node.identifier.name, node)
        }
    }


    @Override
    BaseNode get(String name) {
        def node = nameToChildMap.get(name)
        if (node) {
            return node
        }
        null
    }

    @Override
    void addChild(BaseNode node) {
        super.addChild(node)
        children.add(node)
        updateMap()
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
            updateMap()
        }
    }

    @Override
    void cleanDirtyState() {
        updateMap()
    }
}
