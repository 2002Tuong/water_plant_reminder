package com.example.waterplant.database.entities.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.waterplant.database.entities.Plant;
import com.example.waterplant.database.entities.WaterDay;

import java.util.List;

public class PlantWithWaterDay {
    @Embedded
    Plant plant;
    @Relation(
            parentColumn = "id",
            entityColumn = "id"
    )
    List<WaterDay> waterDays;
}
