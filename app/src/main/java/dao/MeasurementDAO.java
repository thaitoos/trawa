package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import model.MeasurementEntity;

@Dao
public interface MeasurementDAO {
    @Insert
    void insert(MeasurementEntity measurement);

    @Query("SELECT * FROM measurement ORDER BY time")
    LiveData<List<MeasurementEntity>> getAllMeasurementsInOrder();

    @Query("SELECT * FROM measurement WHERE activityStartTime = :id ORDER BY time")
    List<MeasurementEntity> getMeasurementsByActivityId(long id);

}
