package com.sargent.mark.todolist.data;

/**
 * Created by mark on 7/4/17.
 */

public class ToDoItem {
    private String description;
    private String dueDate;
    private String category;//The category obviously
    private boolean taskstatus;// The  task status obviously

    public ToDoItem(String description, String dueDate, String category, boolean taskstatus) {
        this.description = description;
        this.dueDate = dueDate;
        this.category = category;
        this.taskstatus = taskstatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setCategory(String category) {this.category = category;}
    public String getCategory() {return category;}

    public void setTaskStatus(boolean status) {this.taskstatus = status;}
    public boolean getTaskStatus() {return taskstatus;}

}
