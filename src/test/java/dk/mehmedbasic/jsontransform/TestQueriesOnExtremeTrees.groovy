package dk.mehmedbasic.jsontransform

import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.conversion.InlineIdsNamingStrategy
import dk.mehmedbasic.jsonast.conversion.JacksonConverter
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Before
import org.junit.Test

/**
 * A test of a quite large tree.
 */
class TestQueriesOnExtremeTrees {
    JsonDocument document


    @Before
    void prepare() {
        def mapper = new ObjectMapper()

        JsonNode tree = null
        TaskTimer.timeTaken("Reading tree") {
            tree = mapper.readTree(new FileInputStream(new File("c:/large-file.json")))
        }

        TaskTimer.timeTaken("Converted to transformable tree") {
            document = JacksonConverter.asTransformable(tree, new InlineIdsNamingStrategy())
        }

    }

    @Test
    public void testSelectByName() throws Exception {
        timeQuery("node")
    }

    @Test
    public void testSelectByNameMultipleTimes() throws Exception {
        for (Iterable<String> set : ["node", "john", "cake", "foo"].permutations()) {
            timeQuery(set.join(" "))
            timeQuery(set.join(" > "))
        }
    }

    @Test
    public void testSelectById() throws Exception {
        timeQuery("#45719")
    }

    private void timeQuery(String query) {
        TaskTimer.timeTaken("Selected '$query'") {
            def select = document.select("$query")
            println("Query count ${select.length}")
        }
    }

    @Test
    public void testSubQueries() throws Exception {
        timeQuery("person > name > lastName")
    }


}
