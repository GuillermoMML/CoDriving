package com.example.codriving.Homepage.data

import com.example.codriving.data.Car
import com.example.codriving.data.RentCars
import java.time.LocalDate

class HomeState {
    fun getFeaturedCarById(id: Int): RentCars? {
        return featuredCars.find { it.id == id } // Return a default empty car if not found
    }

    fun getFeaturedCarBy(): List<RentCars> {
        return featuredCars // Return a default empty car if not found
    }

    private val featuredCars: List<RentCars> = listOf(
        RentCars(
            Car(
                "ABC-123",
                "Toyota",
                "Corolla",
                2020,
                50000,
                arrayOf("https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/toyota-g1-hero-2-rgb-388457.jpeg?tf=1200x", "https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/toyota-g1-hero-2-rgb-388457.jpeg?tf=1200x")
            ),
            id = 1,
            pricePerDay =20.0,
            startDate = LocalDate.of(2024,2,28),
            endDate = LocalDate.of(2024,3,4),
            rating = 3.3
        ),
        RentCars(
            Car(
                "ABC-123",
                "Toyota",
                "Corolla",
                2020,
                50000,
                arrayOf("https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/toyota-g1-hero-2-rgb-388457.jpeg?tf=1200x", "https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/toyota-g1-hero-2-rgb-388457.jpeg?tf=1200x")
            ),
            id = 2,
            pricePerDay =20.0,
            startDate = LocalDate.of(2024,2,28),
            endDate = LocalDate.of(2024,3,4),
            rating = 3.3

        ),
        RentCars(
            Car(
                "ABC-123",
                "Toyota",
                "Corolla",
                2020,
                50000,
                arrayOf("https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/toyota-g1-hero-2-rgb-388457.jpeg?tf=1200x", "https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/toyota-g1-hero-2-rgb-388457.jpeg?tf=1200x")
            ),
            id = 3,
            pricePerDay =20.0,
            startDate = LocalDate.of(2024,2,28),
            endDate = LocalDate.of(2024,3,4),
            rating = 3.3

        ),
        RentCars(
            Car(
                "ABC-123",
                "Toyota",
                "Corolla",
                2020,
                50000,
                 arrayOf("https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/toyota-g1-hero-2-rgb-388457.jpeg?tf=1200x", "https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/toyota-g1-hero-2-rgb-388457.jpeg?tf=1200x")
            ),
            id = 4,
            pricePerDay =20.0,
            startDate = LocalDate.of(2024,2,28),
            endDate = LocalDate.of(2024,3,4),
            rating = 3.3

        )
    )
}

