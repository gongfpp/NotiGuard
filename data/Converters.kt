package com.mass.notiguard.data

import androidx.room.TypeConverter
import com.mass.notiguard.domain.MatchMode
import com.mass.notiguard.domain.SourceType

class Converters {
    @TypeConverter
    fun matchModeToString(v: MatchMode): String = v.name

    @TypeConverter
    fun stringToMatchMode(v: String): MatchMode = MatchMode.valueOf(v)

    @TypeConverter
    fun sourceTypeToString(v: SourceType): String = v.name

    @TypeConverter
    fun stringToSourceType(v: String): SourceType = SourceType.valueOf(v)
}
