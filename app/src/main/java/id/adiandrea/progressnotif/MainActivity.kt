package id.adiandrea.progressnotif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.RemoteViews
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationLayout: RemoteViews
    private lateinit var customNotification: NotificationCompat.Builder
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        createNotificationChannel()

        findViewById<Button>(R.id.btn).setOnClickListener {
            createNotification()
        }
        findViewById<Button>(R.id.update).setOnClickListener {
            updateNotification()
        }
    }

    private fun updateNotification() {
        notificationLayout.setTextViewText(R.id.notification_title, "${counter++}")

        lifecycleScope.launch {
            for (i in 0 until 100) {
                delay(2000)
                counter += 1
                notificationLayout.setTextViewText(R.id.notification_title, "$counter")
                notificationManager.notify(123, customNotification.build())
            }
        }
    }

    private fun createNotification() {
        notificationLayout = RemoteViews(packageName, R.layout.custom_notification)
        customNotification = NotificationCompat.Builder(applicationContext, "TESTNOTIF")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)

        notificationManager.notify(123, customNotification.build())
    }

    private fun createNotificationChannel() {
        val name = "TEST"
        val descriptionText = "ONLY FOR TEST"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("TESTNOTIF", name, importance).apply {
            description = descriptionText
        }
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}