package com.example.absolutecinema.data.helpers

import android.util.Log
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

fun randomNumber():Int{
    val dayOfYear = LocalDate.now().dayOfYear

    val random = Random(dayOfYear.toLong()).nextInt(1, 100)
    Log.d("Rnum","$random")
    return random

}
