package id.adiandrea.progressnotif

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RemoteViews
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

class MainActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationLayout: RemoteViews
    private lateinit var notificationLayoutExpanded: RemoteViews
    private lateinit var customNotification: NotificationCompat.Builder
    private var progress: Int = 0
    private var maxProgress: Int = 100

    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS
    )

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            createNotification()
        } else {
            Toast.makeText(
                this,
                "Please give permission to post notifications!",
                Toast.LENGTH_SHORT
            ).show()
            ActivityCompat.requestPermissions(
                this@MainActivity, REQUIRED_PERMISSIONS, 100
            )
        }
    }

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                createNotification()
            }
        }
    }

    private fun updateNotification() {
        progress += 25
        Log.i("PROGRESS", "$progress")

        notificationLayout.removeAllViews(R.id.root_progress)
        notificationLayoutExpanded.removeAllViews(R.id.root_progress)

        //add rounded view to the progress bar on the left side
        notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_corner_left))
        notificationLayoutExpanded.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_corner_left))

        repeat(progress){
            notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_left))
            notificationLayoutExpanded.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_left))
        }

        if (progress == maxProgress) {
            notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_thumb_finish))
            notificationLayoutExpanded.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_thumb_finish))
        } else {
            notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_thumb))
            notificationLayoutExpanded.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_thumb))
        }
        repeat(maxProgress - progress) {
            notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_right))
            notificationLayoutExpanded.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_right))
        }

        if (progress < maxProgress) {
            //add rounded view to the progress bar on the right side
            notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_corner_right))
            notificationLayoutExpanded.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_corner_right))
        }

        notificationManager.notify(123, customNotification.build())
    }

    private fun createNotification() {
        progress = 0
        notificationLayout = RemoteViews(packageName, R.layout.custom_notification)
        notificationLayoutExpanded = RemoteViews(packageName, R.layout.custom_notification_expanded)

        //add rounded view to the progress bar on the left side
        notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_corner_left))
        notificationLayoutExpanded.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_corner_left))

        repeat(progress){
            notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_left))
            notificationLayoutExpanded.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_left))
        }
        notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_thumb))
        notificationLayoutExpanded.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_thumb))
        repeat(maxProgress - progress) {
            notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_right))
            notificationLayoutExpanded.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_right))
        }

        //add rounded view to the progress bar on the right side
        notificationLayout.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_corner_right))
        notificationLayoutExpanded.addView(R.id.root_progress, RemoteViews(packageName, R.layout.image_corner_right))

        customNotification = NotificationCompat.Builder(applicationContext, "TESTNOTIF")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)

        notificationManager.notify(123, customNotification.build())
        Log.i("PROGRESS", "$progress")

        lifecycleScope.launch {
            while (progress < maxProgress) {
                delay(3000L)
                updateNotification()
            }
        }

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