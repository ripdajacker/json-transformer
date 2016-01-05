package dk.mehmedbasic.jsontransform

import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.conversion.JacksonConverter
import dk.mehmedbasic.jsonast.transform.VersionDefinition
import org.codehaus.jackson.map.ObjectMapper
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * A small testsuite of version definitions
 */
class TestVersionDefinitions {
    JsonDocument document

    @Before
    void prepare() {
        document = JsonDocument.parse(new FileInputStream(new File("src/main/resources/thesis-example.json")))
    }

    @Test
    void simpleDefinition() {
        def definition = new VersionDefinition(1, """transform("name").renameTo("test").apply()""")
        definition.execute(document)
    }

    @Test
    void scriptDefinition() {
        def stream = new FileInputStream(new File("src/main/resources/test-version1.groovy"))
        def definition = VersionDefinition.parse(stream)
        definition.execute(document)

        Assert.assertTrue(document.selectSingle("billy").present)
    }


    @Test
    void multipleDefinitions() {
        List<VersionDefinition> definitions = []
        for (File file : new File("src/main/resources/transformation_series_1").listFiles()) {
            definitions << VersionDefinition.parse(file.newInputStream())
        }

        println("Before transformations:")
        printOut()
        println("")


        for (VersionDefinition definition : definitions) {
            println("Executing version ${definition.versionNumber}")
            println("Comment: ${definition.comment}")

            definition.execute(document)

            printOut()
        }
        println("")

    }

    @After
    void printOut() {
        def node = JacksonConverter.asJacksonNode(document)
        def string = new ObjectMapper().writer().writeValueAsString(node)
        println(string)
        println()
    }

}
