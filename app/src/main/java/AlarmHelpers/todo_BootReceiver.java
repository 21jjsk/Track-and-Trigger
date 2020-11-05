package AlarmHelpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import Database.DatabaseManagerTodo;
import Objects.todo_Reminder;

public class todo_BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("BOOT_TODO", "onreceive before if!");


        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Log.e("BOOT_TODO", "Boot Receiver OnReceive triggered!");
            DatabaseManagerTodo db = new DatabaseManagerTodo(context);
            todo_AlarmReceiver alarmReceiver = new todo_AlarmReceiver();

            List<todo_Reminder> reminders = db.getPendingReminders();

            for (todo_Reminder rm : reminders) {
                // Cancel existing notification of the reminder by using its ID
                alarmReceiver.cancelAlarm(context, rm.getTaskID());

                // Create a new notification
                alarmReceiver.setAlarm(context, rm, db.getListIDfromTaskID(rm.getTaskID()));
            }

            Log.e("BOOT_TODO", "Boot Receiver OnReceive finished!");
        }
    }
}