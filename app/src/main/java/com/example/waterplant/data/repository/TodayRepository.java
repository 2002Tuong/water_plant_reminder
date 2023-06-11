package com.example.waterplant.data.repository;

import com.example.waterplant.data.database.entities.Today;

public interface TodayRepository {
    void insert(Today today);
    void update(Today today);
    void deleteAll();
}
