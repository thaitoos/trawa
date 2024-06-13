package repo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import dao.ActivityDAO;
import dao.MeasurementDAO;
import database.AppRoomDatabase;
import model.ActivityEntity;

public class ActivityRepo {
    private final ActivityDAO activityDAO;
    private final MeasurementDAO measurementDAO;

    private final LiveData<List<ActivityEntity>> allActivities;

    public ActivityRepo(Application application) {
        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
        activityDAO = db.activityDAO();
        measurementDAO = db.measurementDAO();
        allActivities = activityDAO.getAllActivitiesInOrder();
    }

    public LiveData<List<ActivityEntity>> getAllActivities() {
        return allActivities;
    }

    public void insert(ActivityEntity activity) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> activityDAO.insert(activity));
    }

    public void update(ActivityEntity activity) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> activityDAO.update(activity));
    }

    public void deleteActivity(long startTime) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            activityDAO.deleteActivity(startTime);
            measurementDAO.deleteMeasurementsByActivityId(startTime);
        });
    }

    public List<ActivityEntity> getAllActivitiesList() {
        return activityDAO.getAllActivitiesList();
    }

    public ActivityEntity getActivityById(long startTime) {
        return activityDAO.getActivityByStartTime(startTime);
    }

}
