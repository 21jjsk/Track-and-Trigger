package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Adapters.todo_ListItemTaskAdapter;
import Database.DatabaseManagerTodo;
import Objects.todo_TaskObject;

public class todo_ImportantTasks extends AppCompatActivity {

    List<todo_TaskObject> tasksList;

    RecyclerView tasksRecycler;
    todo_ListItemTaskAdapter tasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_tasks);

        Slidr.attach(this);

        assignUIcomponents();

        loadFromDB();
    }

    private void assignUIcomponents() {
        tasksList = new ArrayList<>();

        tasksRecycler = findViewById(R.id.impTasks_tasks_recycler);
        tasksRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));

        tasksAdapter = new todo_ListItemTaskAdapter(getBaseContext(), R.layout.item_task, tasksList);
        tasksRecycler.setAdapter(tasksAdapter);

        new ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(tasksRecycler);
    }

    private void loadFromDB() {

        Calendar c = Calendar.getInstance();

        int month = c.get(Calendar.MONTH) + 1;

        String date = c.get(Calendar.DAY_OF_MONTH) + "-" + month + "-" + c.get(Calendar.YEAR);

        DatabaseManagerTodo db = new DatabaseManagerTodo(getApplicationContext());
        Cursor cursor = db.getImportantTasks();

        todo_TaskObject task;

        if (cursor.moveToFirst()) {
            do {
                // your code like get columns
                task = new todo_TaskObject(cursor.getString(1), "1".equals(cursor.getString(2)), "1".equals(cursor.getString(4)));
                task.setID(cursor.getInt(0));
                task.setMarkedImportant("1".equals(cursor.getString(6)));
                task.setDate(cursor.getString(4));
                task.setTime(cursor.getString(5));
                tasksList.add(task);
            }
            while (cursor.moveToNext());
        }

        Log.e("tasks size", "today_size: " + tasksList.size());
        if (tasksList.size() > 0) {
            findViewById(R.id.impTasks_no_imp_tasks_static_text).setVisibility(View.GONE);
            tasksAdapter.notifyDataSetChanged();
        }
    }

    private ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            DatabaseManagerTodo db = new DatabaseManagerTodo(getBaseContext());
            db.removeTask(tasksList.get(viewHolder.getAdapterPosition()).getID());
            tasksList.remove(viewHolder.getAdapterPosition());
            tasksAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        }
    };
}