package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonNodes
import dk.mehmedbasic.jsonast.selector.JsonSelectionEngine
import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import groovy.util.logging.Log

/**
 * TODO - someone remind me to document this class 
 *
 * @author Jesenko Mehmedbasic
 * created 10/15/2015.
 */
@TypeChecked
@PackageScope
@Log
final class Mover implements TransformStrategy {
    final String selector

    Mover(String selector) {
        this.selector = selector
    }

    @Override
    void apply(JsonNodes root) {
        for (BaseNode moving : root.roots) {
            def parents = new JsonNodes()
            parents.addExclusion(moving)
            for (BaseNode parent : moving.parents()) {
                parents.addRoot(parent)
            }

            def parser = new JsonSelectionEngine(selector)

            def parsedSelector = parser.parse()
            def newDestinations = parser.execute(parsedSelector, parents)

            if (newDestinations.length > 1) {
                log.warning("Found more than one potential destination for ($moving, $selector)")
            }
            if (newDestinations.length == 0) {
                log.warning("Found zero potential destinations for ($moving, $selector)")
            }
            if (newDestinations.length == 1) {
                def destination = newDestinations.roots[0]
                moving.changeParent(destination)
            }
        }

    }
}
