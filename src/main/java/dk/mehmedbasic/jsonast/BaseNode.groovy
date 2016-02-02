package dk.mehmedbasic.jsonast

import groovy.transform.CompileStatic

/**
 * The base Json node that is the superclass for all nodes in the system.
 * <br/><br/>
 * All nodes are mutable, to accommodate the transformation logic.<br/>
 * This means that every transformer().apply() will potentially change the tree.<br/>
 */
@CompileStatic
abstract class BaseNode implements NodeChangedListener {
    JsonIdentifier identifier = new JsonIdentifier()
    BaseNode parent
    NodeChangedListener listener

    /**
     * Whether or not this is a value node.
     *
     * @return true or false.
     */
    boolean isValueNode() {
        false
    }

    /**
     * Whether or not this node is an array.
     *
     * @return true or false.
     */
    boolean isArray() {
        false
    }

    /**
     * Whether or not this node is a object.
     *
     * @return true or false.
     */
    boolean isObject() {
        false
    }

    /**
     * Whether or not this node is a string value.
     *
     * @return true or false.
     */
    boolean isString() {
        false
    }

    /**
     * Whether or not this node is a boolean value.
     *
     * @return true or false.
     */
    boolean isBoolean() {
        false
    }

    /**
     * Whether or not this node is a integer value.
     *
     * @return true or false.
     */
    boolean isInt() {
        false
    }

    /**
     * Whether or not this node is a double value.
     *
     * @return true or false.
     */
    boolean isDouble() {
        false
    }

    /**
     * Gets a child node by index.
     *
     * @param index the index of the child.
     * @return the child node with the given index.
     */
    BaseNode get(int index) {
        throw new UnsupportedOperationException("Not valid method for nodes of type ${getClass()}")
    }

    /**
     * Gets a child node by name.
     *
     * @param name the name of the child.
     * @return the child node with the given name.
     */
    BaseNode get(String name) {
        throw new UnsupportedOperationException("Not valid method for nodes of type ${getClass()}")
    }

    /**
     * Adds a child node.
     *
     * @param node the node to add.
     */
    void addChild(BaseNode node) {
        node.parent = this
        node.listener = listener
        nodeChanged(NodeChangeEventType.NodeAdded, node)
        nodeChanged(NodeChangeEventType.NodeChanged, this)
    }

    /**
     * Removes a node child node.
     *
     * @param node the node to remove.
     */
    void removeNode(BaseNode node) {
        if (node.parent == this) {
            nodeChanged(NodeChangeEventType.NodeDeleted, node)
        }
    }

    /**
     * Removes a node given a name.
     *
     * @param name the name of the node.
     */
    void removeNode(String name) {
        BaseNode node = get(name)
        if (node) {
            removeNode(node)
        }
    }

    /**
     * Removes a node given an index.
     *
     * @param index the index of the node.
     */
    void removeNode(int index) {
        BaseNode node = get(index)
        if (node) {
            removeNode(node)
        }
    }

    /**
     * Renames a node.
     *
     * @param node the node to rename.
     * @param name the new name of the node.
     */
    void renameNode(BaseNode node, String name) {
        if (node.parent == this) {
            node.identifier.name = name
        }
    }

    /**
     * Renames a child node given a name.
     *
     * @param childName the name of the node.
     * @param newName the new name of the node.
     */
    void renameNode(String childName, String newName) {
        def node = get(childName)
        if (node) {
            renameNode(node, newName)
        }
    }

    @Override
    void nodeChanged(NodeChangeEventType type, BaseNode node) {
        if (listener) {
            listener.nodeChanged(type, node)
        }
    }

    /**
     * Changes the parent of this node to the given parent.
     *
     * @param newParent the new parent.
     */
    void changeParent(BaseNode newParent) {
        if (parent) {
            parent.removeNode(this)
            parent = newParent
        }
        if (newParent) {
            newParent.addChild(this)
        }
        nodeChanged(NodeChangeEventType.NodeChanged, this)
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

    /**
     * Called when the nodes subtree is dirty.
     */
    void cleanDirtyState() {
    }

    /**
     * Calculates the number of jumps to the common ancestor to the given node.
     *
     * @param baseNode the node in question.
     * @return the number of jumps to the first common ancestor.
     */
    int commonAncestor(BaseNode baseNode) {
        def thisParents = parents()
        def thatParents = baseNode.parents()

        int previousSize = Math.max(thisParents.size(), thatParents.size())

        thisParents.retainAll(thatParents)

        previousSize - thisParents.size()
    }
}
