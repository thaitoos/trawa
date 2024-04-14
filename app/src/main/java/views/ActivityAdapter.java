package views;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import model.ActivityEntity;

public class ActivityAdapter extends ListAdapter<ActivityEntity, ActivityViewHolder> {
    public ActivityAdapter(@NonNull DiffUtil.ItemCallback<ActivityEntity> diffCallback) {
        super(diffCallback);
    }

    @Override
    public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ActivityViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position) {
        ActivityEntity current = getItem(position);
        holder.bind(String.valueOf(current.getName()), String.valueOf(current.getDistance()),
                String.valueOf(current.getDuration()), current.getStartTime());
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
}
