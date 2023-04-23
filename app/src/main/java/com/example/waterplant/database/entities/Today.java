package com.example.waterplant.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "to_day")
public class Today {
    @PrimaryKey
    @NonNull
    private int id;
    @NonNull
    private String date;

    @Ignore
    public Today(@NonNull String date) {
        this.date = date;
    }

    public Today(int id, @NonNull String date) {
        this.id = id;
        this.date = date;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
