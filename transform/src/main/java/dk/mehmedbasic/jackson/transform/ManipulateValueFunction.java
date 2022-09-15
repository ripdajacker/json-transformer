package dk.mehmedbasic.jackson.transform;

import dk.mehmedbasic.jackson.JsonValueNode;

/**
 * A function called when values are to be manipulated.
 * <br/><br/>
 * The code manipulates the nodes in-place.
 */
public interface ManipulateValueFunction {

  /**
   * Applies the manipulation.
   *
   * @param node the node to manipulate. Never <code>null</code>.
   */
  void apply(JsonValueNode node);
}
