package dk.mehmedbasic.jsontransform

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
        ownParserRun("src/main/resources/file_1_million.json", 20)
    }


    @Test
    void jacksonAndConverterOneMillion() {
        def file = "src/main/resources/file_1_million.json"

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
        runs.times {
            TaskTimer.timeTaken("Converting to BaseNode document") {
                JacksonConverter.asTransformable(root)
            }
        }
    }


    private static void ownParserRun(String file, int runs) {
        def parser = new BaseNodeParser(new InlineIdsNamingStrategy())

        runs.times {
            TaskTimer.timeTaken("Reading BaseNode tree") {
                parser.parse(new FileInputStream(new File(file)))
            }
        }
    }
}
