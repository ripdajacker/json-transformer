package dk.mehmedbasic.jsontransform

import dk.mehmedbasic.jsonast.JsonDocument
import groovy.transform.TypeChecked
import org.junit.Before
import org.junit.Test

/**
 * Distance tests
 */
@TypeChecked
class DistanceAlgorithm {
    JsonDocument document

    @Before
    void prepare() {
        document = JsonDocument.parse(new FileInputStream(new File("src/main/resources/distance-test.json")))
    }

    @Test
    void stuff() {
        println("Calculating distance")
        def eNode = document.selectSingle("E").get()

        assert eNode.distanceTo(document.selectSingle("Y").get()) == 5
        assert eNode.distanceTo(document.selectSingle("Z").get()) == 4
        assert eNode.distanceTo(document.selectSingle("X").get()) == 3
        assert eNode.distanceTo(document.selectSingle("H").get()) == 2
    }

}
