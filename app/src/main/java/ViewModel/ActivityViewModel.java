package ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import model.ActivityEntity;
import model.ActivityType;
import repo.ActivityRepo;

public class ActivityViewModel extends AndroidViewModel {
    private ActivityRepo repository;
    private LiveData<List<ActivityEntity>> allActivities;

    public ActivityViewModel(Application application) {
        super(application);
        repository = new ActivityRepo(application);
        allActivities = repository.getAllActivities();
    }

    public LiveData<List<ActivityEntity>> getAllActivities() {
        return allActivities;
    }

    public List<ActivityEntity> getAllActivitiesList() {
        return repository.getAllActivitiesList();
    }

    public void insert(ActivityEntity activity) {
        repository.insert(activity);
    }

    public void deleteActivity(long startTime) {
        repository.deleteActivity(startTime);
    }

    public ActivityEntity getActivityById(long startTime) {
        return repository.getActivityById(startTime);
    }
}
