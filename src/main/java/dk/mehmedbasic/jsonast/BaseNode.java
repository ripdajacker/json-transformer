package dk.mehmedbasic.jsonast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The base Json node that is the superclass for all nodes in the system.
 * <br/><br/>
 * All nodes are mutable, to accommodate the transformation logic.<br/>
 * This means that every transformer().apply() will potentially change the tree.<br/>
 */
public abstract class BaseNode {

  public JsonIdentifier getIdentifier() {
    return identifier;
  }

  public void setIdentifier(JsonIdentifier identifier) {
    this.identifier = identifier;
  }

  public BaseNode getParent() {
    return parent;
  }

  public void setParent(BaseNode parent) {
    this.parent = parent;
  }

  private JsonIdentifier identifier = new JsonIdentifier();
  private BaseNode parent;

  /**
   * Whether this is a value node.
   *
   * @return true or false.
   */
  public boolean isValueNode() {
    return false;
  }

  /**
   * Whether this node is an array.
   *
   * @return true or false.
   */
  public boolean isArray() {
    return false;
  }

  /**
   * Whether this node is a object.
   *
   * @return true or false.
   */
  public boolean isObject() {
    return false;
  }

  /**
   * Whether or not this node is a string value.
   *
   * @return true or false.
   */
  public boolean isString() {
    return false;
  }

  /**
   * Whether this node is a boolean value.
   *
   * @return true or false.
   */
  public boolean isBoolean() {
    return false;
  }

  /**
   * Whether this node is a integer value.
   *
   * @return true or false.
   */
  public boolean isInt() {
    return false;
  }

  /**
   * Whether this node is a double value.
   *
   * @return true or false.
   */
  public boolean isDouble() {
    return false;
  }

  /**
   * Gets a child node by index.
   *
   * @param index the index of the child.
   * @return the child node with the given index.
   */
  public BaseNode get(int index) {
    throw new UnsupportedOperationException("Not valid method for nodes of type " + getClass());
  }

  /**
   * Gets a child node by name.
   *
   * @param name the name of the child.
   * @return the child node with the given name.
   */
  public BaseNode get(String name) {
    throw new UnsupportedOperationException("Not valid method for nodes of type " + getClass());
  }

  /**
   * Adds a child node.
   *
   * @param node the node to add.
   */
  public void addChild(BaseNode node) {
    node.setParent(this);
  }

  /**
   * Removes a node child node.
   *
   * @param node the node to remove.
   */
  public void removeNode(BaseNode node) {
  }

  /**
   * Renames a node.
   *
   * @param node the node to rename.
   * @param name the new name of the node.
   */
  public void renameNode(BaseNode node, String name) {
    if (node.getParent().equals(this)) {
      node.getIdentifier().setName(name);
    }
  }

  /**
   * Renames a child node given a name.
   *
   * @param childName the name of the node.
   * @param newName the new name of the node.
   */
  public void renameNode(String childName, String newName) {
    BaseNode node = get(childName);
    if (node != null) {
      renameNode(node, newName);
    }
  }

  /**
   * Changes the parent of this node to the given parent.
   *
   * @param newParent the new parent.
   */
  public void changeParent(BaseNode newParent) {
    if (parent != null) {
      parent.removeNode(this);
      parent = newParent;
    }

    if (newParent != null) {
      newParent.addChild(this);
    }
  }

  public List<BaseNode> parents() {
    List<BaseNode> list = new ArrayList<>();

    BaseNode current = parent;
    while (current != null) {
      list.add(current);
      current = current.getParent();
    }

    return list;
  }

  /**
   * Calculates the distance between this nodes and the given node.
   *
   * @param that the node in question.
   * @return the number of jumps to the first common ancestor.
   */
  public int distanceTo(BaseNode that) {
    List<BaseNode> parents = parents();

    if (parents.contains(that)) {
      return parents.indexOf(that);
    } else {
      List<BaseNode> thatParents = that.parents();

      Collections.reverse(parents);
      Collections.reverse(thatParents);

      int cutoff = Math.min(parents.size(), thatParents.size());

      List<BaseNode> thisCut = parents.subList(0, cutoff);
      List<BaseNode> thatCut = thatParents.subList(0, cutoff);

      var lca = findLcaAndDepth(thisCut, thatCut);

      int distanceToRoot = parents.size() + thatParents.size();
      int twoLca = 2 * lca.distance();
      return distanceToRoot - twoLca - 1;
    }
  }

  /**
   * Finds the lowest common ancestor.
   *
   * @param thisCut this parents,
   */
  private static NodeDistance findLcaAndDepth(List<BaseNode> thisCut,
      List<BaseNode> thatCut) {
    BaseNode previous = null;
    for (int i = 0; i < thatCut.size(); i++) {
      BaseNode a = thisCut.get(i);
      BaseNode b = thatCut.get(i);

      if (a != b) {
        return new NodeDistance(previous, i - 1);
      }

      previous = a;
    }

    return new NodeDistance(previous, thatCut.size() - 1);
  }
}
