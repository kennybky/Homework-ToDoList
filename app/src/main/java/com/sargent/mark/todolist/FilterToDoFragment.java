package com.sargent.mark.todolist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by kenny on 7/13/2017.
 */

public class FilterToDoFragment extends DialogFragment  {
ArrayList<Integer> mSelectedItems;

//An interface that defines the dialog close method, implemented in the main activity
    public interface OnDialogCloseListener {
        void closeFilterDialog(ArrayList<CharSequence> items);
    }

    public FilterToDoFragment() {

    }




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedItems = new ArrayList();  // Where we track the selected items
        for (Integer f : FilterState.checkedItems) {
            if(!mSelectedItems.contains(f))
                mSelectedItems.add(f);//Add the previously selected filters to the list of selected items
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Filter categories")
        // Set the dialog title

                // Specify the list array, the items to be selected by default,
                // and the listener through which to receive callbacks when items are selected(partly copied from developer.android.com)
                .setMultiChoiceItems(R.array.cat_array, FilterState.getCheckedItems(),
                        new DialogInterface.OnMultiChoiceClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {

                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));

                                }
                            }
                        })
                // Set the action buttons

                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results in Filterstate class
                        // and return them to the Main activity
                       /* for (Integer f : FilterState.checkedItems) {
                            if(!mSelectedItems.contains(f))
                            mSelectedItems.add(f);
                        }*/
                        FilterState.checkedItems.clear();//clears the current filter state
                        FilterState.count = 0;//sets the number of filters to 0

                        //gets the number of filters selected and stores them in filter count
                        FilterState.count = getResources().getStringArray(R.array.cat_array).length;

                        //initializes a list view(From stackoverflow.com)
                        ListView lw = ((AlertDialog)dialog).getListView();
                        ArrayList<CharSequence> items = new ArrayList<>();
                        //adds all the categories selected to the array list
                        for(Integer i : mSelectedItems) {
                            FilterState.checkedItems.add(i);
                            items.add((String) lw.getAdapter().getItem(i));
                        }
                        //Initializes the close dialog event listener and calls the method defined in the interface
                        OnDialogCloseListener activity = (OnDialogCloseListener) getActivity();
                        activity.closeFilterDialog(items);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        FilterToDoFragment.this.dismiss();
                    }
                });//Dismisses the dialog if cancel is pressed and leaves the filter state as is


        return builder.create();
    }
    //This static class stores the current filters you selected.
    private static class FilterState {
   static ArrayList<Integer> checkedItems = new ArrayList<>();
    static int count = 0;
        public static boolean[] getCheckedItems() {
            if (checkedItems.isEmpty()) return null;
            else {

                boolean[] items = new boolean[count];
                for (int i = 0; i < items.length; i++) {
                    items[i] = false;
                }

                for (Integer i : checkedItems) {
                    items[i] = true;
                }

                return items;
            }
        }
    }

}


