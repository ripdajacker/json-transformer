package dk.mehmedbasic.jsontransform

import dk.mehmedbasic.jsonast.JsonDocument
import dk.mehmedbasic.jsonast.conversion.BaseNodeParser
import dk.mehmedbasic.jsonast.conversion.InlineIdsNamingStrategy
import dk.mehmedbasic.jsonast.conversion.JacksonConverter
import groovy.transform.TypeChecked
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.ObjectMapper
import org.junit.Test

/**
 * The base node parser test
 */
@TypeChecked
class TestBaseNodeParser {
    @Test
    void ownParserTestOneMillion() {
        ownParserRun("src/main/resources/large_files/file_1_million.json", 20)
    }


    @Test
    void jacksonAndConverterOneMillion() {
        def file = "src/main/resources/large_files/file_1_million.json"

        JsonNode tree = readTree(file)
        convertJackson(tree, 20)
    }

//    @Test
    void ownParserTest22Million() {
        ownParserRun("src/main/resources/large_files/file_22_million.json", 20)
    }

//    @Test
    void jacksonAndConverter22Million() {
        def file = "src/main/resources/large_files/file_22_million.json"

        JsonNode tree = readTree(file)
        convertJackson(tree, 20)
    }

    private static JsonNode readTree(String file) {
        def mapper = new ObjectMapper()

        JsonNode tree = null

        TaskTimer.timeTaken("Reading tree") {
            tree = mapper.readTree(new FileInputStream(new File(file)))
        }
        tree
    }

    private static void convertJackson(JsonNode root, int runs) {
        for (int i = 0; i < runs; i++) {
            TaskTimer.timeTaken("Converting to BaseNode document") {
                JsonDocument document = JacksonConverter.asTransformable(root)

            }
        }
    }


    private static void ownParserRun(String file, int runs) {
        def parser = new BaseNodeParser(new InlineIdsNamingStrategy())

        for (int i = 0; i < runs; i++) {
            TaskTimer.timeTaken("Reading BaseNode tree") {
                parser.parse(new FileInputStream(new File(file)))
            }
        }
    }
}
