package views;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import ViewModel.ActivityViewModel;
import model.ActivityEntity;

public class ActivityAdapter extends ListAdapter<ActivityEntity, ActivityViewHolder> {
    ActivityViewModel activityViewModel;
    public ActivityAdapter(@NonNull DiffUtil.ItemCallback<ActivityEntity> diffCallback, ActivityViewModel activityViewModel) {
        super(diffCallback);
        this.activityViewModel = activityViewModel;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ActivityViewHolder.create(parent, activityViewModel);
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position) {
        ActivityEntity current = getItem(position);
        long seconds = current.getDuration() / 1000;
        holder.bind(String.valueOf(current.getName()), round(current.getDistance(), 2) + " km",
                String.format("%02dm:%02ds", seconds / 60, seconds % 60),
                current.getStartTime());
    }

    @Override
    public long getItemId(int position) {
        ActivityEntity activityEntity = getItem(position);
        return activityEntity.getStartTime();
    }

    static public class WordDiff extends DiffUtil.ItemCallback<ActivityEntity> {

        @Override
        public boolean areItemsTheSame(@NonNull ActivityEntity oldItem, @NonNull ActivityEntity newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ActivityEntity oldItem, @NonNull ActivityEntity newItem) {
            return oldItem.getName().equals(newItem.getName()) && oldItem.getDistance() == newItem.getDistance() &&
                    oldItem.getDuration() == newItem.getDuration();
        }
    }

    public static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
