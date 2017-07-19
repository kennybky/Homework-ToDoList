package com.sargent.mark.todolist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.sargent.mark.todolist.data.Contract;
import com.sargent.mark.todolist.data.DBHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FilterToDoFragment.OnDialogCloseListener ,AddToDoFragment.OnDialogCloseListener, UpdateToDoFragment.OnUpdateDialogCloseListener{

    private RecyclerView rv;
    private FloatingActionButton button;
    private FloatingActionButton filter;
    private DBHelper helper;
    private Cursor cursor;
    private SQLiteDatabase db;
    ToDoListAdapter adapter;
    ArrayList<CharSequence> filters = new ArrayList<>();
    private final String TAG = "mainactivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "oncreate called in main activity");
        rv = (RecyclerView) findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        if(savedInstanceState!=null){
            filters = savedInstanceState.getCharSequenceArrayList("filters");
            helper = new DBHelper(this);
            db = helper.getWritableDatabase();
            if(filters!=null)
            cursor = doFilters(filters);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putCharSequenceArrayList("filters", filters);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        filters = savedInstanceState.getCharSequenceArrayList("filters");

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemNumber = item.getItemId();
        if (itemNumber == R.id.addToDo) {
            FragmentManager fm = getSupportFragmentManager();
            AddToDoFragment frag = new AddToDoFragment();
            frag.show(fm, "addtodofragment");

        } else if(itemNumber == R.id.filterToDo) {
            FragmentManager fm = getSupportFragmentManager();
            FilterToDoFragment newFragment = new FilterToDoFragment();
            newFragment.show(fm, "tag");
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (db != null) db.close();
        if (cursor != null) cursor.close();
    }




    @Override
    protected void onStart() {
        super.onStart();

        helper = new DBHelper(this);
        db = helper.getWritableDatabase();
        if(cursor == null)
        cursor = getAllItems(db);
        else if(cursor.isClosed())
            cursor = doFilters(filters);

        adapter = new ToDoListAdapter(cursor, new ToDoListAdapter.ItemClickListener() {
            @Override
            public void onLongPress(View view, long id) {
                ContentValues cv = new ContentValues();
                cv.put(Contract.TABLE_TODO.COLUMN_NAME_TASK_STATUS, 1);

                db.update(Contract.TABLE_TODO.TABLE_NAME, cv, Contract.TABLE_TODO._ID + "=" + id, null);
                cursor = getAllItems(db);
                adapter.swapCursor(cursor);
            }

            @Override
            public void onItemClick(int pos, String description, String duedate, long id, String category) {
                Log.d(TAG, "item click id: " + id);
                String[] dateInfo = duedate.split("-");
                int year = Integer.parseInt(dateInfo[0].replaceAll("\\s",""));
                int month = Integer.parseInt(dateInfo[1].replaceAll("\\s",""));
                int day = Integer.parseInt(dateInfo[2].replaceAll("\\s",""));

                FragmentManager fm = getSupportFragmentManager();

                UpdateToDoFragment frag = UpdateToDoFragment.newInstance(year, month, day, description, id, category);
                frag.show(fm, "updatetodofragment");
            }


        });

        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                Log.d(TAG, "passing id: " + id);
                removeToDo(db, id);
                adapter.swapCursor(getAllItems(db));
            }





        }).attachToRecyclerView(rv);
    }

    @Override
    public void closeDialog(int year, int month, int day, String description, String category) {
        addToDo(db, description, formatDate(year, month, day), category);
        cursor = getAllItems(db);
        adapter.swapCursor(cursor);
    }

    public String formatDate(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }



    private Cursor getAllItems(SQLiteDatabase db) {
        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }

    private long addToDo(SQLiteDatabase db, String description, String duedate, String category) {
        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY, category);
        return db.insert(Contract.TABLE_TODO.TABLE_NAME, null, cv);
    }

    private boolean removeToDo(SQLiteDatabase db, long id) {
        Log.d(TAG, "deleting id: " + id);
        return db.delete(Contract.TABLE_TODO.TABLE_NAME, Contract.TABLE_TODO._ID + "=" + id, null) > 0;
    }


    private int updateToDo(SQLiteDatabase db, int year, int month, int day, String description, long id, String category){

        String duedate = formatDate(year, month - 1, day);

        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);

        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY, category);

        return db.update(Contract.TABLE_TODO.TABLE_NAME, cv, Contract.TABLE_TODO._ID + "=" + id, null);
    }

    @Override
    public void closeUpdateDialog(int year, int month, int day, String description, long id, String category) {
        updateToDo(db, year, month, day, description, id, category);
        adapter.swapCursor(getAllItems(db));
    }

    //Called when the filter dialog is closed
    @Override
    public void closeFilterDialog(ArrayList<CharSequence> items) {
        //If the items are not empty filter the to-do displayed else just display all the items
        filters = items;
           cursor = doFilters(items);
            adapter.swapCursor(cursor);
    }

    public Cursor doFilters(ArrayList<CharSequence> items) {
        if(!items.isEmpty()) {

            String[] args = new String[items.size()];
            items.toArray(args);
            final String myQuery = " SELECT * FROM " + Contract.TABLE_TODO.TABLE_NAME +
                    " WHERE " + Contract.TABLE_TODO.COLUMN_NAME_CATEGORY + " IN (" + makePlaceholders(args.length) + ") ORDER BY " +
                    Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE; //Construct the query to display only the categories in the array
            cursor = db.rawQuery(myQuery, args); //calls the query and creates a cursor that points to the result
            return cursor;
        }else{
            cursor = getAllItems(db);
            return cursor;
        }
    }

    // Makes place holders (?) for the database query
    public String makePlaceholders(int l) {
        if (l < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("?");
            for (int i = 1; i < l; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }
}
