package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AnimeStyle
import com.example.data.model.AspectRatio
import com.example.data.model.CameraMotionType
import com.example.data.model.NarrationType
import com.example.data.model.TransitionType

class AnimeConverters {
    @TypeConverter
    fun fromAspectRatio(value: AspectRatio): String = value.name

    @TypeConverter
    fun toAspectRatio(value: String): AspectRatio = try {
        AspectRatio.valueOf(value)
    } catch (_: Exception) {
        AspectRatio.SHORTS_9_16
    }

    @TypeConverter
    fun fromAnimeStyle(value: AnimeStyle): String = value.name

    @TypeConverter
    fun toAnimeStyle(value: String): AnimeStyle = try {
        AnimeStyle.valueOf(value)
    } catch (_: Exception) {
        AnimeStyle.ANIME_2D
    }

    @TypeConverter
    fun fromCameraMotionType(value: CameraMotionType): String = value.name

    @TypeConverter
    fun toCameraMotionType(value: String): CameraMotionType = try {
        CameraMotionType.valueOf(value)
    } catch (_: Exception) {
        CameraMotionType.STATIC
    }

    @TypeConverter
    fun fromTransitionType(value: TransitionType): String = value.name

    @TypeConverter
    fun toTransitionType(value: String): TransitionType = try {
        TransitionType.valueOf(value)
    } catch (_: Exception) {
        TransitionType.FADE
    }

    @TypeConverter
    fun fromNarrationType(value: NarrationType): String = value.name

    @TypeConverter
    fun toNarrationType(value: String): NarrationType = try {
        NarrationType.valueOf(value)
    } catch (_: Exception) {
        NarrationType.LOCAL_TTS
    }
}
