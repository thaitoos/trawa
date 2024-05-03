package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import model.ActivityEntity;

@Dao
public interface ActivityDAO {
    @Insert
    void insert(ActivityEntity activity);

    @Query("SELECT * FROM activity ORDER BY startTime DESC")
    LiveData<List<ActivityEntity>> getAllActivitiesInOrder();

    @Update
    void update(ActivityEntity activity);

    @Query("SELECT * FROM activity WHERE startTime = :startTime")
    LiveData<ActivityEntity> getActivityByStartTime(long startTime);

    @Query("DELETE FROM activity WHERE startTime = :startTime")
    void deleteActivity(long startTime);

    @Query("SELECT * FROM activity")
    List<ActivityEntity> getAllActivitiesList();
}
