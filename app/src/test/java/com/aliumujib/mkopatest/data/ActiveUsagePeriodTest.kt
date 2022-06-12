package com.aliumujib.mkopatest.data

import com.google.common.truth.Truth
import org.junit.Test
import java.time.OffsetDateTime


class ActiveUsagePeriodTest {
    @Test
    fun getSecondsTillExpiry_returns_correct_results() {
        //GIVEN
        val currentTime =  OffsetDateTime.now().withHour(23).withMinute(57).withSecond(59)
        val activeUsagePeriod = ActiveUsagePeriod(OffsetDateTime.now().withHour(23).withMinute(59).withSecond(59))
        //WHEN
        val actual = activeUsagePeriod.getSecondsTillExpiry(currentTime.toEpochSecond())
        //THEN
        Truth.assertThat(actual).isEqualTo(120)
    }
}