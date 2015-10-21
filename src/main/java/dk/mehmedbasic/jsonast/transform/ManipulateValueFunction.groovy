package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.JsonValueNode
import groovy.transform.TypeChecked

/**
 * A function called when values are to be manipulated.
 * <br/><br/>
 * The code manipulates the nodes in-place.
 */
@TypeChecked
interface ManipulateValueFunction {
    /**
     * Applies the manipulation.
     *
     * @param node the node to manipulate. Never <code>null</code>.
     */
    void apply(JsonValueNode node)
}
