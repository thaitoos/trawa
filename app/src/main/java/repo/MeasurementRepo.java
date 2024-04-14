package repo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import dao.MeasurementDAO;
import database.AppRoomDatabase;
import model.MeasurementEntity;

public class MeasurementRepo {
    private MeasurementDAO measurementDAO;
    private LiveData<List<MeasurementEntity>> allMeasurements;

    public MeasurementRepo(Application application) {
        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
        measurementDAO = db.measurementDAO();
        allMeasurements = measurementDAO.getAllMeasurementsInOrder();
    }

    public LiveData<List<MeasurementEntity>> getAllMeasurements() {
        return allMeasurements;
    }

    public List<MeasurementEntity> getMeasurementsByActivityId(long id) {
        return measurementDAO.getMeasurementsByActivityId(id);
    }

    public void insert(MeasurementEntity measurement) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            measurementDAO.insert(measurement);
        });
    }
}
