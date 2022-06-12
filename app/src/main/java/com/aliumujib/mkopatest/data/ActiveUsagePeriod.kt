package com.aliumujib.mkopatest.data

import java.time.OffsetDateTime

class ActiveUsagePeriod(private val usageExpiry: OffsetDateTime) {
    fun getSecondsTillExpiry(currentTimeEpoch: Long = OffsetDateTime.now().toEpochSecond()): Long {
        return usageExpiry.toEpochSecond() - currentTimeEpoch
    }
}