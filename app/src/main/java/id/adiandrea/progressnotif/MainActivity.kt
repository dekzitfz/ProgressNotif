package id.adiandrea.progressnotif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RemoteViews
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationLayout: RemoteViews
    private lateinit var customNotification: NotificationCompat.Builder
    private var progress: Int = 0
    private var maxProgress: Int = 100

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
    }

    private fun updateNotification() {
        while (progress < maxProgress) {
            runBlocking {
                delay(2000L)
                progress += 25
                Log.i("PROGRESS", "$progress")

                notificationLayout.removeAllViews(R.id.root_progress)
                repeat(progress){
                    notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_left))
                }
                notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_thumb))
                repeat(maxProgress - progress) {
                    notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_right))
                }

                notificationManager.notify(123, customNotification.build())
            }
        }
    }

    private fun createNotification() {
        notificationLayout = RemoteViews(packageName, R.layout.custom_notification)
        repeat(progress){
            notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_left))
        }
        notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_thumb))
        repeat(maxProgress - progress) {
            notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_right))
        }
        customNotification = NotificationCompat.Builder(applicationContext, "TESTNOTIF")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)

        notificationManager.notify(123, customNotification.build())
        Log.i("PROGRESS", "$progress")

        updateNotification()
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