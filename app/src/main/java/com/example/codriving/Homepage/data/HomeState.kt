package com.example.codriving.Homepage.data

import com.example.codriving.data.RentCars
;
import java.time.LocalDate

class HomeState{
    fun getFeaturedCarById(id: Int): RentCars? {
        return featuredCars.find { it.id == id } // Return a default empty car if not found
    }

    fun getFeaturedCarBy(): List<RentCars> {
        return featuredCars // Return a default empty car if not found
    }
    private val featuredCars: List<RentCars> = listOf(
        RentCars(
            id = 1,
            brand = "Toyota",
            model = "Corolla",
            year = 2022,
            image = arrayOf("https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/Toyota_Highlander.jpg?tf=1200x"),
            kilometros = 5000,
            pricePerDay = 50.0,
            startDate = LocalDate.now(),
            endDate = LocalDate.of(2024, 2, 25)
        ),
        RentCars(
            id = 2,
            brand = "Honda",
            model = "Civic",
            year = 2023,
            image = arrayOf("https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/Toyota_Highlander.jpg?tf=1200x"),
            kilometros = 8000,
            pricePerDay = 60.0,
            startDate = LocalDate.of(2024, 2, 21),
            endDate = LocalDate.of(2024, 2, 26)
        ),

        RentCars(
            id = 2,
            brand = "Audi",
            model = "Audi R8",
            year = 2023,
            image = arrayOf("https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/Toyota_Highlander.jpg?tf=1200x"),
            kilometros = 5000,
            pricePerDay = 60.0,
            startDate = LocalDate.of(2024, 2, 21),
            endDate = LocalDate.of(2024, 2, 26)
        ),

        RentCars(
            id = 2,
            brand = "Audi",
            model = "Audi R8",
            year = 2023,
            image = arrayOf("https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/Toyota_Highlander.jpg?tf=1200x"),
            kilometros = 5000,
            pricePerDay = 60.0,
            startDate = LocalDate.of(2024, 2, 21),
            endDate = LocalDate.of(2024, 2, 26)
        ),
        RentCars(
            id = 2,
            brand = "Audi",
            model = "Audi R8",
            year = 2023,
            image = arrayOf("https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/Toyota_Highlander.jpg?tf=1200x"),
            kilometros = 5000,
            pricePerDay = 60.0,
            startDate = LocalDate.of(2024, 2, 21),
            endDate = LocalDate.of(2024, 2, 26)
        ),
    )
}

