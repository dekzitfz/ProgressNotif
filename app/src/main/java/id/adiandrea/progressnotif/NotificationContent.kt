package id.adiandrea.progressnotif

data class NotificationContent(
    var status: String = "",
    var eta: String = "",
    var infoDesc: String = "",
    var progress: Int = 0,
)