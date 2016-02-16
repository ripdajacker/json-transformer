package dk.mehmedbasic.jsontransform

import org.codehaus.jackson.JsonGenerator
import org.codehaus.jackson.map.ObjectMapper

/**
 * Generates random json
 */
class RandomJsonGenerator {
    static List<String> names = ["node", "cake", "john", "fun", "horse", "hancock", "foobar", "stein", "lol", "portapotty", "askepot"]

    static void generate(File output, int roots, int children, int levels) {
        println("generating ${Math.pow(children, levels) * roots} nodes")

        def mapper = new ObjectMapper()
        JsonGenerator generator = mapper.jsonFactory.createJsonGenerator(new FileWriter(output))

        for (int i = 0; i < roots; i++) {
            generator.writeStartObject()
            generateChild(generator, 0, children, levels)
            generator.writeEndObject()
        }

        generator.close()
    }

    static long count = 0

    static void generateChild(JsonGenerator generator, int currentLevel, int children, int levels) {
        if (currentLevel >= levels) {
            return
        }

        for (int i = 0; i < children; i++) {
            generator.writeFieldName(names[i])

            generator.writeStartObject()
            count++
            generateChild(generator, currentLevel + 1, children, levels)
            generator.writeEndObject()
        }
    }

    public static void main(String[] args) {
        generate(new File("src/main/resources/large_files/file__million.json"), 2, 10, 7)
        println("Count: $count")
    }
}
