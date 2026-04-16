package com.henrydev.faithsteward.data.db

import androidx.room.TypeConverter
import com.henrydev.faithsteward.domain.model.ChallengeStatus

class ChallengeConverters {
    @TypeConverter
    fun fromStatus(status: ChallengeStatus): String {
        return status.name
    }
    @TypeConverter
    fun toStatus(value: String): ChallengeStatus {
        return ChallengeStatus.valueOf(value)
    }
}