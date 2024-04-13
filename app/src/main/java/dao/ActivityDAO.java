package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import model.ActivityEntity;

@Dao
public interface ActivityDAO {
    @Insert
    void insert(ActivityEntity activity);

    @Query("SELECT * FROM activity ORDER BY startTime")
    LiveData<List<ActivityEntity>> getAllActivitiesInOrder();

    @Update
    void update(ActivityEntity activity);

    @Query("SELECT * FROM activity WHERE startTime = :startTime")
    LiveData<ActivityEntity> getActivityByStartTime(long startTime);
}
