package com.example.nmm.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.nmm.models.Scores;

import java.util.List;

@Dao
public interface MainDAO {

    @Insert(onConflict = REPLACE)
    void insert(Scores scores);

    @Query("SELECT * FROM scores ORDER BY id DESC")
    List<Scores> getAll();

    @Query("UPDATE scores SET title = :title, winner = :winner WHERE id = :id")
    void update(int id, String title, String winner);

    @Delete
    void delete(Scores scores);
}
