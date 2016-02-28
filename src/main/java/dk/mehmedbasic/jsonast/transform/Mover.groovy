package dk.mehmedbasic.jsonast.transform

import dk.mehmedbasic.jsonast.BaseNode
import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonNodes
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j

/**
 * Moves nodes from one place to another.
 */
@Slf4j
@PackageScope
@CompileStatic
final class Mover extends TransformationFunction {
    final String selector

    Mover(String selector) {
        this.selector = selector
    }

    @Override
    void apply(JsonDocument document, JsonNodes root) {
        def queryRoot = document.select(null).withCaching()

        List<Tuple2<BaseNode, BaseNode>> changes = []

        log.info("Attempting move of ${root.size()} elements")

        for (BaseNode source : root.roots) {
            queryRoot.exclusions.clear()
            queryRoot.exclusions.add(source)

            def newDestinations = queryRoot.select(selector)

            BaseNode destination = null

            if (newDestinations.empty) {
                log.debug("Found zero potential destinations for ($source, $selector)")
            } else if (newDestinations.size() > 1) {
                def closest = newDestinations.closestTo(source)
                if (closest.size() == 0) {
                    log.debug("Found ambiguous destinations for ($source, $selector)")
                } else if (closest.size() > 1) {
                    log.debug("Found more than one potential destination for ($source, $selector)")
                } else {
                    destination = closest.first().first
                }
            } else {
                destination = newDestinations.roots.iterator().next()
            }

            if (destination != null) {
                changes << new Tuple2<BaseNode, BaseNode>(source, destination)
            }
        }

        changes.each {
            def source = it.first
            def destination = it.second

            source.changeParent(destination)
        }
    }
}
