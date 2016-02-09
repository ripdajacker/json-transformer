package dk.mehmedbasic.jsontransform

import org.codehaus.jackson.JsonGenerator
import org.codehaus.jackson.map.ObjectMapper

/**
 * TODO[JEKM] - someone remind me to document this class.
 */
class RandomJsonGenerator {
    static List<String> names = ["node", "cake","john","fun"]

    static void generate(File output, int children, int levels) {
        println("generating ${Math.pow(children, levels)} nodes")

        def mapper = new ObjectMapper()
        JsonGenerator generator = mapper.jsonFactory.createJsonGenerator(new FileWriter(output))
        generator.writeStartObject()
        generateChild(generator, 0, children, levels)
        generator.writeEndObject()
        generator.close()
    }

    static void generateChild(JsonGenerator generator, int currentLevel, int children, int levels) {
        if (currentLevel > levels) {
            return
        }

        for (int i = 0; i < children; i++) {
            generator.writeFieldName(names[i])

            generator.writeStartObject()
            generateChild(generator, currentLevel + 1, children, levels)
            generator.writeEndObject()
        }
    }

    static String random(List<String> input) {
        return input[new Random().nextInt(input.size())]
    }

    public static void main(String[] args) {
        generate(new File("c:/large-file.json"), 4, 10)
    }
}
