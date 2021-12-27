package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        createChannel(CHANNEL_ID, "Download Notification")

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {

            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            custom_button.hasCompletedDownload()



            val query = id?.let { DownloadManager.Query().setFilterById(it) }
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val cursor: Cursor = downloadManager.query(query)
            cursor.moveToFirst()
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))


            Log.i("MainActivity", "received download status: " + status)
            notificationManager.sendNotification(getStatus(status), applicationContext)
        }
    }

    private fun getStatus(status: Int): String {
        if (status == DownloadManager.STATUS_FAILED) {
            return "Failed"
        } else if (status == DownloadManager.STATUS_PAUSED) {
            return "Paused"
        } else if (status == DownloadManager.STATUS_PENDING) {
            return "Pending"
        } else if (status == DownloadManager.STATUS_RUNNING) {
            return "Running"
        } else {
            return "Successful"
        }
    }

    private fun download() {


        val radioButtonId = getRadioButtonId()
        custom_button.optionSelected = radioButtonId


        if (radioButtonId != -1) {
            val request =
                DownloadManager.Request(Uri.parse(getUrl(getRadioButtonText())))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        }

    }

    private fun getRadioButtonId(): Int {
        return downloadGroup.checkedRadioButtonId
    }

    private fun getRadioButtonText(): String {
        val radioButtonId = getRadioButtonId()
        val radioButton = findViewById<RadioButton>(radioButtonId)
        return radioButton.text.toString()
    }

    private fun getUrl(selectedOption: String): String? {
        if (selectedOption == "Glide - Image Loading") {
            return "https://github.com/bumptech/glide"
        } else if(selectedOption == "Retroifit") {
            return "https://github.com/square/retrofit"
        } else {
            return "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        }
    }

    fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
        val intent  = Intent(applicationContext, DetailActivity::class.java).apply {
            putExtra("selected_option", getRadioButtonText())
            putExtra("status", messageBody)
        }
        pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_description))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notify(0, builder.build())

    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(true)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_button)

            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
