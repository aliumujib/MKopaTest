package com.aliumujib.mkopatest.data

import kotlinx.coroutines.delay
import java.time.OffsetDateTime

interface ActiveUsagePeriodDataSource {
    suspend fun getLockingInfo(): ActiveUsagePeriod
}

class ActiveUsagePeriodDataSourceImpl : ActiveUsagePeriodDataSource {
    override suspend fun getLockingInfo(): ActiveUsagePeriod {
        delay(500) // Simulate a network call
        return ActiveUsagePeriod(
            OffsetDateTime.now().withHour(23).withMinute(59).withSecond(59)
        )
    }
}