package com.sargent.mark.todolist;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sargent.mark.todolist.data.Contract;
import com.sargent.mark.todolist.data.ToDoItem;

import java.util.ArrayList;

/**
 * Created by mark on 7/4/17.
 */

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ItemHolder> {

    private Cursor cursor;
    private ItemClickListener listener;
    private String TAG = "todolistadapter";

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item, parent, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bind(holder, position);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public interface ItemClickListener {
        void onItemClick(int pos, String description, String duedate, long id, String category);
        void onLongPress(View view, long id);
    }

    public ToDoListAdapter(Cursor cursor, ItemClickListener listener) {
        this.cursor = cursor;
        this.listener = listener;
    }

    public void swapCursor(Cursor newCursor){
        if (cursor != null) cursor.close();
        cursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener  {
        TextView descr;



        TextView due;
        String duedate;
        String description;
        ImageView check;
        long id;
        String category;


        ItemHolder(View view) {
            super(view);
            descr = (TextView) view.findViewById(R.id.description);
            due = (TextView) view.findViewById(R.id.dueDate);
            check = (ImageView) view.findViewById(R.id.taskdone);
            view.setOnClickListener(this);

            view.setOnLongClickListener(this);
        }

        public void bind(ItemHolder holder, int pos) {
            cursor.moveToPosition(pos);
            id = cursor.getLong(cursor.getColumnIndex(Contract.TABLE_TODO._ID));
            Log.d(TAG, "deleting id: " + id);

            duedate = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE));
            description = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION));
            category = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY));
            descr.setText(description);

            boolean done = cursor.getInt(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_TASK_STATUS)) == 1;

           //Mark the element as done if it is done


            due.setText(duedate);
            //If done is true set the image view to the check button else leave it blank
            if(done){
                check.setImageResource(R.drawable.ic_check_circle_black_24dp);

            } else{
                check.setImageResource(0);
            }
            holder.itemView.setTag(id);
        }





        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            listener.onItemClick(pos, description, duedate, id, category);
        }

        //If the item is long clicked, it marks the item as done
        @Override
        public boolean onLongClick(View view) {
            listener.onLongPress(view, id);
            return true;
        }


    }

}
