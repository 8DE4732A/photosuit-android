package win.liuping.photosuit_android.domain.model

data class ExifData(
    val make: String? = null,
    val model: String? = null,
    val focalLength: String? = null,
    val aperture: String? = null,
    val shutterSpeed: String? = null,
    val iso: String? = null,
    val dateTime: String? = null,
    val gpsLatitude: String? = null,
    val gpsLongitude: String? = null,
    val lensModel: String? = null,
    val exposureMode: String? = null,
    val whiteBalance: String? = null,
    val flash: String? = null,
    val width: Int? = null,
    val height: Int? = null,
) {
    val cameraName: String get() = listOfNotNull(make, model).joinToString(" ").trim()

    val cameraLogoKey: String? get() = when {
        make == null -> null
        make.contains("canon", ignoreCase = true) -> "Canon"
        make.contains("nikon", ignoreCase = true) -> "Nikon"
        make.contains("sony", ignoreCase = true) -> "Sony"
        make.contains("fuji", ignoreCase = true) -> "Fujifilm"
        make.contains("olympus", ignoreCase = true) -> "Olympus"
        make.contains("panasonic", ignoreCase = true) -> "Lumix"
        make.contains("leica", ignoreCase = true) -> "Leica"
        make.contains("hasselblad", ignoreCase = true) -> "Hasselblad"
        make.contains("dji", ignoreCase = true) -> "DJI"
        make.contains("gopro", ignoreCase = true) -> "GoPro"
        make.contains("apple", ignoreCase = true) -> "Apple"
        make.contains("samsung", ignoreCase = true) -> "Samsung"
        make.contains("huawei", ignoreCase = true) -> "Huawei"
        make.contains("xiaomi", ignoreCase = true) -> "Xiaomi"
        make.contains("oppo", ignoreCase = true) -> "OPPO"
        make.contains("vivo", ignoreCase = true) -> "Vivo"
        make.contains("oneplus", ignoreCase = true) -> "Oneplus"
        make.contains("google", ignoreCase = true) -> "Google"
        make.contains("ricoh", ignoreCase = true) -> "Ricoh"
        make.contains("pentax", ignoreCase = true) -> "Pentax"
        make.contains("sigma", ignoreCase = true) -> "Sigma"
        make.contains("insta360", ignoreCase = true) -> "Insta360"
        make.contains("nokia", ignoreCase = true) -> "Nokia"
        else -> null
    }
}
