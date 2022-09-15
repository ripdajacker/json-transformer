package transformation_series_1

version 1
comment "Renames the 'name' property to 'billy' and adds a dog named bingo."

transformations {

    transform("name")
            .renameTo("billy")
            .apply()

    transform("person")
            .addJson("dog", '{"type":"dog", "name": "bingo"}')
            .apply()
}

