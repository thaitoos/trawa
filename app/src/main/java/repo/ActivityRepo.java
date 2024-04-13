package repo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import dao.ActivityDAO;
import database.AppRoomDatabase;
import model.ActivityEntity;

public class ActivityRepo {
    private ActivityDAO activityDAO;
    private LiveData<List<ActivityEntity>> allActivities;

    public ActivityRepo(Application application) {
        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
        activityDAO = db.activityDAO();
        allActivities = activityDAO.getAllActivitiesInOrder();
    }

    public LiveData<List<ActivityEntity>> getAllActivities() {
        return allActivities;
    }

    public void insert(ActivityEntity activity) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            activityDAO.insert(activity);
        });
    }

    public void update(ActivityEntity activity) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            activityDAO.update(activity);
        });
    }

}
