package dk.mehmedbasic.jsonast

import groovy.transform.TypeChecked

/**
 * The base Json node that is the superclass for all nodes in the system.
 * <br/><br/>
 * All nodes are mutable, to accommodate the transformation logic.<br/>
 * This means that every transformer().apply() will potentially change the tree.<br/>
 */
@TypeChecked
abstract class BaseNode {
    JsonIdentifier identifier = new JsonIdentifier()
    BaseNode parent
    NodeChangedListener listener

    boolean isValueNode() {
        false
    }

    boolean isContainerNode() {
        false
    }

    boolean isArray() {
        false
    }

    boolean isObject() {
        false
    }

    boolean isString() {
        false
    }

    boolean isBoolean() {
        false
    }

    boolean isInt() {
        false
    }

    boolean isDouble() {
        false
    }


    BaseNode get(int index) {
        throw new UnsupportedOperationException("Not valid method for nodes of type ${getClass()}")
    }

    BaseNode get(String name) {
        throw new UnsupportedOperationException("Not valid method for nodes of type ${getClass()}")
    }

    void addChild(BaseNode node) {
        node.parent = this

        nodeChanged(NodeChangeEventType.ChildrenChanged, this)
        nodeChanged(NodeChangeEventType.ParentChanged, node)
    }

    void removeNode(BaseNode node) {
        if (node.parent == this) {
            nodeChanged(NodeChangeEventType.ChildrenChanged, this)
        }
    }

    void removeNode(String name) {
        BaseNode node = get(name)
        if (node) {
            removeNode(node)
        }
    }

    void renameNode(BaseNode node, String name) {
        if (node.parent == this) {
            node.identifier.name = name
            nodeChanged(NodeChangeEventType.IdentifierChanged, node)
        }
    }

    void renameChild(String childName, String newName) {
        def node = get(childName)
        if (node) {
            renameNode(node, newName)
        }
    }

    void nodeChanged(NodeChangeEventType type, BaseNode node) {
        if (listener) {
            listener.nodeChanged(type, node)
        }
    }

    void changeParent(BaseNode newParent) {
        if (parent) {
            parent.removeNode(this)
            parent = newParent
        }
        if (newParent) {
            newParent.addChild(this)
        }
        nodeChanged(NodeChangeEventType.ParentChanged, this)
    }

    List<BaseNode> parents() {
        def list = []

        BaseNode current = parent
        while (current != null) {
            list.add(current)
            current = current.parent
        }
        return list
    }

    void cleanDirtyState() {

    }

    int commonAncestor(BaseNode baseNode) {
        def thisParents = parents()
        def thatParents = baseNode.parents()

        int previousSize = Math.max(thisParents.size(), thatParents.size())

        thisParents.retainAll(thatParents)

        previousSize - thisParents.size()
    }
}
