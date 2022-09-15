package dk.mehmedbasic.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The base Json node that is the superclass for all nodes in the system.
 *
 * All nodes are mutable, to accommodate the transformation logic.<br/>
 * This means that every transformer().apply() will potentially change the tree.<br/>
 */
public class BaseNode<T> {

  private final NodeId identifier = new NodeId();

  private BaseNode<T> parent;
  private final List<BaseNode<T>> children = new ArrayList<>();
  private final T value;

  public BaseNode(T value) {
    this.value = value;
  }

  public List<BaseNode<T>> getChildren() {
    return children;
  }

  public T getValue() {
    return value;
  }

  public NodeId getIdentifier() {
    return identifier;
  }

  public BaseNode<T> getParent() {
    return parent;
  }

  public void setParent(BaseNode<T> parent) {
    this.parent = parent;
  }

  public int size() {
    return children.size();
  }

  /**
   * Gets a child node by index.
   *
   * @param index the index of the child.
   * @return the child node with the given index.
   */
  public BaseNode<T> get(int index) {
    return children.get(index);
  }

  /**
   * Gets a child node by name.
   *
   * @param name the name of the child.
   * @return the child node with the given name.
   */
  public BaseNode<T> get(String name) {
    for (BaseNode<T> child : children) {
      if (Objects.equals(child.identifier.getName(), name)) {
        return child;
      }
    }
    return null;
  }

  /**
   * Adds a child node.
   *
   * @param node the node to add.
   */
  public void addChild(BaseNode<T> node) {
    node.changeParent(this);
  }

  /**
   * Removes a node child node.
   *
   * @param node the node to remove.
   */
  public void removeChild(BaseNode<T> node) {
    children.remove(node);
  }

  /**
   * Changes the parent of this node to the given parent.
   *
   * @param newParent the new parent.
   */
  public void changeParent(BaseNode<T> newParent) {
    if (parent != null) {
      parent.removeChild(this);
      parent = newParent;
    }

    if (newParent != null) {
      newParent.addChild(this);
    }
  }

  /**
   * Gets the list of nodes all the way to the root of the tree.
   *
   * @return the path to root
   */
  public List<BaseNode<T>> pathToRoot() {
    List<BaseNode<T>> list = new ArrayList<>();

    BaseNode<T> current = parent;
    while (current != null) {
      list.add(current);
      current = current.getParent();
    }

    return list;
  }
}
