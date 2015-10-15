package dk.mehmedbasic.jsonast

import groovy.transform.TypeChecked

/**
 * TODO - someone remind me to document this class 
 *
 * @author Jesenko Mehmedbasic
 * created 10/15/2015.
 */
@TypeChecked
class JsonArrayNode extends BaseNode {
    List<BaseNode> children = new LinkedList<>()

    @Override
    boolean isArray() {
        true
    }

    @Override
    boolean isContainerNode() {
        true
    }

    @Override
    BaseNode get(int index) {
        if (index < children.size()) {
            return children.get(index)
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
}
