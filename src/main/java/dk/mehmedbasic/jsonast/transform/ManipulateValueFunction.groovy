package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.JsonValueNode
import groovy.transform.TypeChecked

/**
 * A function called when values are to be manipulated.
 */
@TypeChecked
interface ManipulateValueFunction {
    void apply(JsonValueNode node)
}
