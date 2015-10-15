package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonNodes
import dk.mehmedbasic.jsonast.JsonValueNode
import groovy.transform.PackageScope
import groovy.transform.TypeChecked

/**
 * Manipulates values
 */
@TypeChecked
@PackageScope
final class Manipulator implements TransformStrategy {
    ManipulateValueFunction function

    Manipulator(ManipulateValueFunction function) {
        this.function = function
    }

    @Override
    void apply(JsonNodes root) {
        if (function) {
            for (BaseNode node : root) {
                if (node.isValueNode()) {
                    function.apply(node as JsonValueNode)
                }
            }
        }
    }
}
