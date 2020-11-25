package AlarmHelpers;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.todo.todo_DueToday;
import com.example.todo.todo_ListActivity;
import com.example.todo.R;


import java.util.Calendar;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import Database.todo_BaseDatabase;
import Database.DatabaseManagerTodo;
import Objects.todo_Reminder;

import static androidx.core.content.ContextCompat.startActivity;

public class todo_AlarmReceiver extends BroadcastReceiver {
    AlarmManager mAlarmManager;
    PendingIntent mPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        int listID = Integer.parseInt(intent.getStringExtra(todo_BaseDatabase.TASKS_PARENT_ID));


        // check if daily alarm for dueToday is received
        if (listID == todo_BaseDatabase.TODAY_ID) {
            daily_due_today_notification(context);
            return;
        }


        // get taskID and Reminder(taskID)
        DatabaseManagerTodo db = new DatabaseManagerTodo(context);
        int taskID = Integer.parseInt(intent.getStringExtra(todo_BaseDatabase.REMINDERS_TASK_ID));
        todo_Reminder reminder = db.getReminder(taskID);


        // creating intent to open ListActivity on clicking the notification
        Intent editIntent = new Intent(context, todo_ListActivity.class);
        editIntent.putExtra(todo_BaseDatabase.TASKS_PARENT_ID, listID);
        PendingIntent mClick = PendingIntent.getActivity(context, reminder.getTaskID(), editIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        //creating intent for action button | mark as done
        Intent actionIntent = new Intent(context, todo_NotificationActionReceiver.class);
        actionIntent.putExtra("ACTION", "mark_as_done");
        actionIntent.putExtra(todo_BaseDatabase.TASKS_ID, taskID + "");
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, taskID, actionIntent, 0);


        //create RemoteViews for custom collapsed notification layout
        RemoteViews collapsedNotification = new RemoteViews(context.getPackageName(), R.layout.notification_collapsed);
        collapsedNotification.setTextViewText(R.id.notif_collapsed_task_description, reminder.getTaskDescription());
        collapsedNotification.setTextViewText(R.id.notif_collapsed_timestamp, reminder.get12hrTimeWithAmPm());
        collapsedNotification.setImageViewResource(R.id.notif_collapsed_icon, db.getListIcon(listID));


        //create RemoteViews for custom collapsed notification layout
        RemoteViews expandedNotification = new RemoteViews(context.getPackageName(), R.layout.notification_expanded);
        expandedNotification.setTextViewText(R.id.notif_expanded_task_description, reminder.getTaskDescription());
        expandedNotification.setTextViewText(R.id.notif_expanded_timestamp, reminder.get12hrTimeWithAmPm());
        expandedNotification.setTextViewText(R.id.notif_expanded_list_name, db.getListName(listID));
        expandedNotification.setImageViewResource(R.id.notif_expanded_icon, db.getListIcon(listID));


        // Build the Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Due Tasks")
                .setSmallIcon(R.drawable.icon_done)
                .setCustomContentView(collapsedNotification)
                .setCustomBigContentView(expandedNotification)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true);


        // Create notification from builder and display it
        Log.e("NOTIF: ", "8");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        builder.setContentIntent(mClick);
        notificationManager.notify(taskID, builder.build());

        //update in database that the task's notification has been sent/displayed
        db.hasBeenNotified(taskID);

        // Editing
            String sEmail = "notification.sender.email@gmail.com";
            String sPassword = "ABC123!!";
            //String To = "21jjsk@gmail.com";
            String To = "heenabaraiya72@gmail.com";
            String Subject = "REMINDER: You have a pending task....";
            String Message = reminder.getTaskDescription();

            Properties properties = new Properties();
            properties.put("mail.smtp.auth","true");
            properties.put("mail.smtp.starttls.enable","true");
            properties.put("mail.smtp.host","smtp.gmail.com");
            properties.put("mail.smtp.port","587");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(sEmail,sPassword);
                }
            });

            try {
                //Initialise Email content
                javax.mail.Message message = new MimeMessage(session);
                //Sender email
                message.setFrom(new InternetAddress(sEmail));
                //Recipient email
                message.setRecipients(javax.mail.Message.RecipientType.TO,InternetAddress.parse(To.trim()));
                //Email subject
                message.setSubject(Subject.trim());
                //Email message
                message.setText(Message.trim());

                //Send email
                new SendMail().execute(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        // Editing
    }
    private class SendMail extends AsyncTask<Message,String,String> {
        //Initialise progress dialog

        @Override
        protected String doInBackground(javax.mail.Message... messages) {
            try {
                //When success
                Transport.send(messages[0]);
                return "Success";
            } catch (MessagingException e) {
                //When error
                e.printStackTrace();
                return "Error";
            }

        }
    }

    public void setAlarm(Context context, todo_Reminder reminder, int listID) {

        if (listID == todo_BaseDatabase.TODAY_ID) {
            mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, todo_AlarmReceiver.class);
            intent.putExtra(todo_BaseDatabase.REMINDERS_TASK_ID, reminder.getTaskID() + "");
            intent.putExtra(todo_BaseDatabase.TASKS_PARENT_ID, listID + "");

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 7);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);

            mPendingIntent = PendingIntent.getBroadcast(context, todo_BaseDatabase.TODAY_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mPendingIntent);

            ComponentName receiver = new ComponentName(context, todo_BootReceiver.class);
            PackageManager pm = context.getPackageManager();
            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

            Log.e("DAILY-ALARM", "SET");
            return;
        }

        Log.e("NOTIF: ", reminder.getTaskID() + "");
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Put Reminder ID in Intent Extra
        Intent intent = new Intent(context, todo_AlarmReceiver.class);
        intent.putExtra(todo_BaseDatabase.REMINDERS_TASK_ID, reminder.getTaskID() + "");
        intent.putExtra(todo_BaseDatabase.TASKS_PARENT_ID, listID + "");
        mPendingIntent = PendingIntent.getBroadcast(context, reminder.getTaskID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.e("NOTIF: ", "3");
        // Start alarm using notification time

        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getCalendar().getTimeInMillis(), mPendingIntent);

        Log.e("NOTIF: ", "4");
        // Restart alarm if device is rebooted
        ComponentName receiver = new ComponentName(context, todo_BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context, int taskID) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancel Alarm using Reminder ID
        mPendingIntent = PendingIntent.getBroadcast(context, taskID, new Intent(context, todo_AlarmReceiver.class), 0);
        mAlarmManager.cancel(mPendingIntent);

        // Disable alarm
        ComponentName receiver = new ComponentName(context, todo_BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void daily_due_today_notification(Context context) {

        Log.e("DAILY-ALARM", "");

        // creating intent to open ListActivity on clicking the notification
        Intent editIntent = new Intent(context, todo_DueToday.class);
        PendingIntent mClick = PendingIntent.getActivity(context, 0, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        // get number of tasks due today
        int dueTodayTasks = new DatabaseManagerTodo(context).getNumberOfTasksDueToday();


        //create RemoteViews for custom notification layout
        RemoteViews collapsedNotification = new RemoteViews(context.getPackageName(), R.layout.notification_collapsed);
        collapsedNotification.setTextViewText(R.id.notif_collapsed_task_description, "You have " + dueTodayTasks + " due today!");
        //collapsedNotification.setViewVisibility(R.id.notif_collapsed_list_name, View.GONE);
        collapsedNotification.setImageViewResource(R.id.notif_collapsed_icon, R.drawable.icon_alarm);


        // Build the Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Due Tasks")
                .setSmallIcon(R.drawable.icon_done)
                .setCustomContentView(collapsedNotification)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true);


        // Create notification from builder and display it
        Log.e("NOTIF: ", "8");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        builder.setContentIntent(mClick);
        notificationManager.notify(todo_BaseDatabase.TODAY_ID, builder.build());
    }

}