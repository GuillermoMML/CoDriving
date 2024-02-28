package com.example.codriving.Homepage.data

import com.example.codriving.data.Car
import com.example.codriving.data.RentCars
import java.time.LocalDate

class HomeState {
    fun getFeaturedCarById(id: Int): RentCars? {
        return featuredCars.find { it.id == id }
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
                listOf(
                    "https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/toyota-g1-hero-2-rgb-388457.jpeg?tf=1200x",
                    "https://cochesnuevos.autofacil.es/img/TOYOTA_AYGO_X_CROSS_2022.jpg"
                )
            ),
            id = 1,
            pricePerDay = 20.0,
            startDate = LocalDate.of(2024, 2, 28),
            endDate = LocalDate.of(2024, 3, 4),
            rating = 3.5,
            ownerName = "Michael"

        ),
        RentCars(
            Car(
                "ABC-123",
                "Toyota",
                "Corolla",
                2020,
                50000,

                listOf(
                    "https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/toyota-g1-hero-2-rgb-388457.jpeg?tf=1200x",
                    "https://cochesnuevos.autofacil.es/img/TOYOTA_AYGO_X_CROSS_2022.jpg"
                )
            ),
            id = 2,
            pricePerDay = 20.0,
            startDate = LocalDate.of(2024, 2, 28),
            endDate = LocalDate.of(2024, 3, 4),
            rating = 3.4,
            ownerName = "Michael"

        ),
        RentCars(
            Car(
                "ABC-123",
                "Toyota",
                "Corolla",
                2020,
                50000,
                listOf(
                    "https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/toyota-g1-hero-2-rgb-388457.jpeg?tf=1200x",
                    "https://cochesnuevos.autofacil.es/img/TOYOTA_AYGO_X_CROSS_2022.jpg"
                )
            ),
            id = 3,
            pricePerDay = 20.0,
            startDate = LocalDate.of(2024, 2, 28),
            endDate = LocalDate.of(2024, 3, 4),
            rating = 3.6,
            ownerName = "Michael"


        ),
        RentCars(
            Car(
                "ABC-123",
                "Toyota",
                "Corolla",
                2020,
                50000,
                listOf(
                    "https://cdn.autobild.es/sites/navi.axelspringer.es/public/bdc/dc/fotos/1038278_170035-car_812Superfast.jpg?tf=3840x",
                    "https://cochesnuevos.autofacil.es/img/TOYOTA_AYGO_X_CROSS_2022.jpg"
                )
            ),
            id = 4,
            pricePerDay = 20.0,
            startDate = LocalDate.of(2024, 2, 28),
            endDate = LocalDate.of(2024, 3, 4),
            rating = 3.6,
            ownerName = "Michael"

        )
    )
}

