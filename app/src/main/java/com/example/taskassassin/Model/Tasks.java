package com.example.taskassassin.Model;

public class Tasks {
    String task;
    String timestamp;

    public Tasks(){}

    public Tasks(String task, String timestamp) {
        this.task = task;
        this.timestamp = timestamp;
    }

    public String getTask() {
        return task;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
