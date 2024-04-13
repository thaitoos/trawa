package ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import model.MeasurementEntity;
import repo.MeasurementRepo;

public class MeasurementViewModel extends AndroidViewModel {
    private MeasurementRepo repository;
    private LiveData<List<MeasurementEntity>> allMeasurements;

    public MeasurementViewModel(Application application) {
        super(application);
        repository = new MeasurementRepo(application);
        allMeasurements = repository.getAllMeasurements();
    }

    public LiveData<List<MeasurementEntity>> getAllMeasurements() {
        return allMeasurements;
    }

    public void insert(MeasurementEntity measurement) {
        repository.insert(measurement);
    }
}
