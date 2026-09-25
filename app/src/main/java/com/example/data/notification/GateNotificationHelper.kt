package com.example.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.ExamMilestoneEntity

object GateNotificationHelper {

    const val CHANNEL_ID_EXAM_ALERTS = "gate_exam_alerts_channel"
    const val CHANNEL_NAME_EXAM_ALERTS = "GATE 2027 Exam Dates & Milestones"

    const val CHANNEL_ID_STUDY_REMINDERS = "gate_study_reminders_channel"
    const val CHANNEL_NAME_STUDY_REMINDERS = "Daily Study & Revision Targets"

    fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val examChannel = NotificationChannel(
                CHANNEL_ID_EXAM_ALERTS,
                CHANNEL_NAME_EXAM_ALERTS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for GATE 2027 application deadlines, admit cards, and exam dates"
                enableVibration(true)
            }

            val studyChannel = NotificationChannel(
                CHANNEL_ID_STUDY_REMINDERS,
                CHANNEL_NAME_STUDY_REMINDERS,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily study targets, streaks, and spaced revision reminders"
            }

            notificationManager.createNotificationChannel(examChannel)
            notificationManager.createNotificationChannel(studyChannel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun sendMilestoneNotification(context: Context, milestone: ExamMilestoneEntity) {
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            milestone.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_EXAM_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("GATE 2027: ${milestone.title}")
            .setContentText("${milestone.dateDisplay}: ${milestone.description}")
            .setStyle(NotificationCompat.BigTextStyle().bigText("${milestone.dateDisplay}\n${milestone.description}"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(milestone.id.hashCode(), builder.build())
    }

    fun sendDailyStudyReminder(context: Context, streakDays: Int, targetHours: Int) {
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            2001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_STUDY_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🔥 Keep your $streakDays-Day GATE Streak Alive!")
            .setContentText("You have $targetHours hours planned for today. Complete your topic revision & solve PYQs.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2001, builder.build())
    }

    fun sendTestExamNotification(context: Context, title: String, message: String) {
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_EXAM_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
