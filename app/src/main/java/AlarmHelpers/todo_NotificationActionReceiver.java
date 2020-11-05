package AlarmHelpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import Database.todo_BaseDatabase;
import Database.DatabaseManagerTodo;

public class todo_NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("ACTION");
        int taskID = Integer.parseInt(intent.getStringExtra(todo_BaseDatabase.TASKS_ID));

        Log.e("NAR", action + " " + taskID);

        if(action.equals("mark_as_done")){
            DatabaseManagerTodo db = new DatabaseManagerTodo(context);
            db.updateTaskToFinished(taskID);
        }
    }
}