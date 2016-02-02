package dk.mehmedbasic.jsonast

import groovy.transform.TypeChecked

/**
 * TODO - someone remind me to document this class 
 */
@TypeChecked
class JsonNodesWithParent extends JsonNodes {

    @Delegate
    JsonNodes parent

    JsonNodesWithParent(JsonNodes parent) {
        super(parent.document)
        this.parent = parent
    }


}
