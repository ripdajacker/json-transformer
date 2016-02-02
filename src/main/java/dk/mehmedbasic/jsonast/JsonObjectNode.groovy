package dk.mehmedbasic.jsonast

import groovy.transform.TypeChecked

/**
 * A Json object node.
 */
@TypeChecked
class JsonObjectNode extends BaseNode {
    LinkedList<BaseNode> children = new LinkedList<>()

    private Map<String, BaseNode> nameToChildMap = new TreeMap<>()


    void updateMap() {
        nameToChildMap = null
    }

    private void initializeMap() {
        if (nameToChildMap == null) {
            nameToChildMap = new TreeMap<>()

            for (BaseNode node : children) {
                nameToChildMap.put(node.identifier.name, node)
            }
        }
    }

    @Override
    BaseNode get(String name) {
        initializeMap()
        def node = nameToChildMap.get(name)
        if (node) {
            return node
        }
        null
    }

    @Override
    void addChild(BaseNode node) {
        addChildNoUpdate(node)
        updateMap()
    }

    void addChildNoUpdate(BaseNode node) {
        super.addChild(node)
        children.add(node)
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

            nodeChanged(NodeChangeEventType.NodeChanged, this)
        }
    }

    @Override
    void cleanDirtyState() {
        updateMap()
    }
}
