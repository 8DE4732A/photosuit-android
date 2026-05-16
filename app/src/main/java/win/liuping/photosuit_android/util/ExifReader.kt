package win.liuping.photosuit_android.util

import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import win.liuping.photosuit_android.domain.model.ExifData
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExifReader @Inject constructor() {

    fun read(context: Context, uri: Uri): ExifData {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val exif = ExifInterface(stream)
                ExifData(
                    make = exif.getAttribute(ExifInterface.TAG_MAKE)?.trim(),
                    model = exif.getAttribute(ExifInterface.TAG_MODEL)?.trim(),
                    focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)?.let { formatFocalLength(it) },
                    aperture = exif.getAttribute(ExifInterface.TAG_F_NUMBER)?.let { "f/$it" },
                    shutterSpeed = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)?.let { formatShutterSpeed(it) },
                    iso = exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS),
                    dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME)?.let { formatDateTime(it) },
                    gpsLatitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE),
                    gpsLongitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE),
                    lensModel = exif.getAttribute(ExifInterface.TAG_LENS_MODEL)?.trim(),
                    exposureMode = exif.getAttribute(ExifInterface.TAG_EXPOSURE_MODE),
                    whiteBalance = exif.getAttribute(ExifInterface.TAG_WHITE_BALANCE),
                    flash = exif.getAttribute(ExifInterface.TAG_FLASH),
                    width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0).takeIf { it > 0 },
                    height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0).takeIf { it > 0 },
                )
            } ?: ExifData()
        } catch (e: IOException) {
            ExifData()
        }
    }

    private fun formatFocalLength(raw: String): String {
        return runCatching {
            if (raw.contains('/')) {
                val parts = raw.split('/')
                val num = parts[0].toFloat()
                val den = parts[1].toFloat()
                if (den == 0f) raw else "${(num / den).toInt()}mm"
            } else {
                "${raw.toFloat().toInt()}mm"
            }
        }.getOrDefault(raw)
    }

    private fun formatShutterSpeed(raw: String): String {
        return runCatching {
            val value = if (raw.contains('/')) {
                val parts = raw.split('/')
                parts[0].toFloat() / parts[1].toFloat()
            } else raw.toFloat()

            when {
                value >= 1f -> "${value.toInt()}s"
                else -> {
                    val denom = (1f / value).toInt()
                    "1/${denom}s"
                }
            }
        }.getOrDefault(raw)
    }

    private fun formatDateTime(raw: String): String {
        return runCatching {
            // Format: "2023:10:15 14:30:00" -> "2023-10-15 14:30"
            raw.replace(':', '-', ignoreCase = false).let {
                val parts = it.split(" ")
                if (parts.size >= 2) {
                    val dateParts = parts[0].split("-")
                    val timeParts = parts[1].split("-")
                    "${dateParts[0]}-${dateParts[1]}-${dateParts[2]} ${timeParts[0]}:${timeParts[1]}"
                } else it
            }
        }.getOrDefault(raw)
    }
}
