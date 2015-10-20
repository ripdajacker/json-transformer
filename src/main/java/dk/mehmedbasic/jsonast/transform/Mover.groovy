package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonNodes
import dk.mehmedbasic.jsonast.selector.JsonSelectionEngine
import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import groovy.util.logging.Log

/**
 * Moves nodes from one place to another.
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
        for (BaseNode source : root.roots) {
            def parents = new JsonNodes()
            parents.addExclusion(source)
            for (BaseNode parent : source.parents()) {
                parents.addRoot(parent)
            }

            def parser = new JsonSelectionEngine(selector)

            def parsedSelector = parser.parse()
            def newDestinations = parser.execute(parsedSelector, parents)
            BaseNode destination = null

            if (newDestinations.length > 1) {
                def closest = newDestinations.closestTo(source)
                if (closest.size() == 0) {
                    log.warning("Found ambiguous destinations for ($source, $selector)")
                } else if (closest.size() > 1) {
                    log.warning("Found more than one potential destination for ($source, $selector)")
                } else {
                    destination = closest.first().first
                }
            } else if (newDestinations.length == 0) {
                log.warning("Found zero potential destinations for ($source, $selector)")
            } else {
                destination = newDestinations.roots.first()
            }

            if (destination) {
                source.changeParent(destination)
            }
        }

    }
}
