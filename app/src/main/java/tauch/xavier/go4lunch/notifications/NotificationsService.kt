package tauch.xavier.go4lunch.notifications


import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Context
import tauch.xavier.go4lunch.activities.MainActivity
import android.content.Intent
import androidx.core.app.NotificationCompat
import tauch.xavier.go4lunch.R


class NotificationsService : FirebaseMessagingService() {

    private val NOTIFICATION_ID = 7
    private val NOTIFICATION_TAG = "GO4LUNCH"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if (remoteMessage!!.notification != null) {
            // Get message sent by Firebase
            val message = remoteMessage.notification!!.body
            // 8 - Show notification after received message
            this.sendVisualNotification(message)
        }
    }

    private fun sendVisualNotification(messageBody: String?) {

        // 1 - Create an Intent that will be shown when user will click on the Notification
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        // 2 - Create a Style for the Notification
        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle(getString(R.string.notification_title)) // Set Notification title
        inboxStyle.addLine(messageBody)

        // 3 - Create a Channel (Android 8)
        val channelId = getString(R.string.default_notification_channel_id)

        // 4 - Build a Notification object
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_title))
            .setAutoCancel(true) // Clear notification after click
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent) // Redirect user to the activity when notification is clicked
            .setStyle(inboxStyle)

        // 5 - Add the Notification to the Notification Manager and show it.
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Message from Firebase"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(mChannel)
        }

        // 7 - Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build())
    }
}