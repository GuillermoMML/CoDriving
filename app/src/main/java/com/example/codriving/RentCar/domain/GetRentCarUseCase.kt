package com.example.codriving.RentCar.domain

import android.database.Observable
import com.example.codriving.data.RentCars

interface GetRentCarUseCase {
    fun execute(id: Int): Observable<RentCars>

}