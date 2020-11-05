package AlarmHelpers;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.example.todo.R;

import java.util.ArrayList;
import java.util.List;

import Adapters.todo_ListItemTaskAdapter;
import Objects.todo_TaskObject;

public class todo_WidgetHelper extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget);

        List<todo_TaskObject> taskObjectsList = new ArrayList<>();

        todo_ListItemTaskAdapter adapter = new todo_ListItemTaskAdapter(context, R.layout.item_task, taskObjectsList);

        appWidgetManager.updateAppWidget(appWidgetIds[0], view);

    }
}