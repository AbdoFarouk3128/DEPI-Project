package com.example.absolutecinema.data.helpers

import android.util.Log
import java.time.LocalDate
import java.time.LocalTime

fun Season():String{
    val time =LocalDate.now().monthValue
    val keywords = when(time){
        1->"65|260365"
        2->"160404|246"
        3->"380|14724"
        4->"4152|3205"
        5->"18035|170173"
        6,7,8->"13088|10349|6273"
        9->"3640|325045|2783"
        10->"13073|162846|18035"
        11->"6075|6054"
        else->"6513|207317"
    }
return keywords
}
