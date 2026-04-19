package com.muhammetkocak.turkcekelimeapp.core.datetime

import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.TimeUnit

const val DAY_MILLIS: Long = 86_400_000L

/**
 * UTC günün başlangıç epoch millisaniyesi. Streak ve günlük aggregate mantığı için.
 */
fun startOfDayUtc(epochMillis: Long): Long {
    val dayIndex = epochMillis / DAY_MILLIS
    return dayIndex * DAY_MILLIS
}

/**
 * Cihazın yerel saatine göre günün başlangıcı (epoch millis). Streak için daha doğal olan bu yöntem.
 */
fun startOfDayLocal(epochMillis: Long, zone: ZoneId = ZoneId.systemDefault()): Long =
    Instant.ofEpochMilli(epochMillis).atZone(zone).toLocalDate()
        .atStartOfDay(zone).toInstant().toEpochMilli()

fun daysBetween(fromEpochMillis: Long, toEpochMillis: Long): Long =
    TimeUnit.MILLISECONDS.toDays(toEpochMillis - fromEpochMillis)
