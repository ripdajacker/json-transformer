package transformation_series_1

import dk.mehmedbasic.jackson.JsonValueNode

version 4
comment "Transform the age into year of birth"

transformations {

    transform("person")
            .renameChild("age", "yearOfBirth")
            .apply()

    transform("person")
            .manipulateValue("yearOfBirth") { JsonValueNode node ->
        int age = node.intValue()

        def instance = Calendar.instance
        instance.add(Calendar.YEAR, -1 * age)

        node.value = instance.get(Calendar.YEAR)
    }.apply()
}

