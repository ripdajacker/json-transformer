package dk.mehmedbasic.jsontransform

import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.conversion.InlineIdsNamingStrategy
import dk.mehmedbasic.jsonast.conversion.JacksonConverter
import groovy.transform.TypeChecked
import groovy.util.logging.Log
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Before
import org.junit.Test

/**
 * A test of a quite large tree.
 */
@TypeChecked
@Log
class TestVeryLargeTrees {
    JsonDocument document

    long end
    long start

    @Before
    void prepare() {
        def mapper = new ObjectMapper()
        start = System.currentTimeMillis()
        log.info("Reading tree")
        def tree = mapper.readTree(new FileInputStream(new File("src/main/resources/example_au_people.json")))

        end = System.currentTimeMillis()
        log.info("Read tree in ${(end - start) / 1000d} seconds")

        start = System.currentTimeMillis()
        document = JacksonConverter.asTransformable(tree, new InlineIdsNamingStrategy())
        end = System.currentTimeMillis()
        log.info("Converted to transformable tree in ${(end - start) / 1000d} seconds")

        start = System.currentTimeMillis()

    }

    @Test
    public void testSelectByName() throws Exception {
        def select = document.select("query")
        end = System.currentTimeMillis()
        log.info("Selected all queries in ${(end - start) / 1000d} seconds")
        log.info("Query count: ${select.nodeCount}")
    }
}
