package dk.mehmedbasic.jsontransform

import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.JsonNodes
import dk.mehmedbasic.jsonast.conversion.JacksonConverter
import dk.mehmedbasic.jsonast.selector.JsonSelectionEngine
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Assert
import org.junit.Test

/**
 * Tests selectors
 */
class TestSelectors {
    @Test
    void shouldReadWithoutException() {
        def mapper = new ObjectMapper()
        def tree = mapper.readTree(new FileInputStream(new File("src/main/resources/move-rename.json")))
        JsonDocument foo = JacksonConverter.convert(tree)
        // This should not fail
    }

    @Test
    void selectByName() {
        def mapper = new ObjectMapper()
        def tree = mapper.readTree(new FileInputStream(new File("src/main/resources/move-rename.json")))
        JsonDocument document = JacksonConverter.convert(tree)

        def parser = new JsonSelectionEngine('name')
        def names = parser.execute(document as JsonNodes)

        Assert.assertEquals("Names should have two elements", 2, names.length)
        Assert.assertEquals("First element should be value 'Jon Snow'", "Jon Snow", names.roots[0].stringValue())
        Assert.assertEquals("Second element should be value 'Aemon Targaryen'", "Aemon Targaryen", names.roots[1].stringValue())
    }
}
