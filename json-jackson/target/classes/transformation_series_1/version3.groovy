package transformation_series_1

version 3
comment "Adds age to the person"

transformations {
    transform("person")
            .add("age", 60)
            .apply()
}

