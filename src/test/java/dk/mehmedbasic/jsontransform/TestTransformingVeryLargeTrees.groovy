package dk.mehmedbasic.jsontransform

import dk.mehmedbasic.jsonast.JsonArrayNode
import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonType
import dk.mehmedbasic.jsonast.conversion.InlineIdsNamingStrategy
import dk.mehmedbasic.jsonast.conversion.JacksonConverter
import groovy.transform.TypeChecked
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * A test of a quite large tree.
 */
@TypeChecked
class TestTransformingVeryLargeTrees {
    JsonDocument document


    @Before
    void prepare() {
        def mapper = new ObjectMapper()

        JsonNode tree = null
        TaskTimer.timeTaken("Reading tree") {
            tree = mapper.readTree(new FileInputStream(new File("src/main/resources/example_au_people.json")))
        }

        TaskTimer.timeTaken("Converted to transformable tree") {
            document = JacksonConverter.asTransformable(tree, new InlineIdsNamingStrategy())
        }
    }

    @Test
    public void movePersonInstanceToArray() throws Exception {

        TaskTimer.timeTaken("Create a collection in the people node") {
            document.transform("people")
                    .add("collection", JsonType.Array)
                    .apply()
        }

        def expectedPeopleCount = document.select("person").length

        TaskTimer.timeTaken("Moving person instances to collection") {
            document.transform("person")
                    .moveTo("collection")
                    .apply()
        }


        def actualPeopleCount = document.selectSingle("collection").get() as JsonArrayNode

        Assert.assertEquals(expectedPeopleCount, actualPeopleCount.children.size())
    }

    @Test
    public void movePersonInstanceToArrayAddTag() throws Exception {

        TaskTimer.timeTaken("Create a collection in the people node") {
            document.transform("people")
                    .add("collection", JsonType.Array)
                    .apply()
        }

        def expectedPeopleCount = document.select("person").length

        TaskTimer.timeTaken("Moving person instances to collection") {
            document.transform("person")
                    .add("@tag", "person")
                    .moveTo("collection")
                    .apply()
        }


        def actualPeopleCount = document.selectSingle("collection").get() as JsonArrayNode
        Assert.assertEquals(expectedPeopleCount, actualPeopleCount.children.size())
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
