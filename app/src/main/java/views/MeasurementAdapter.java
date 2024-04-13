package views;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import model.MeasurementEntity;

public class MeasurementAdapter extends ListAdapter<MeasurementEntity, MeasurementViewHolder> {
    public MeasurementAdapter(@NonNull DiffUtil.ItemCallback<MeasurementEntity> diffCallback) {
        super(diffCallback);
    }

    @Override
    public MeasurementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return MeasurementViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(MeasurementViewHolder holder, int position) {
        MeasurementEntity current = getItem(position);
        holder.bind(String.valueOf(current.getTime()));
    }

    static public class WordDiff extends DiffUtil.ItemCallback<MeasurementEntity> {

        @Override
        public boolean areItemsTheSame(@NonNull MeasurementEntity oldItem, @NonNull MeasurementEntity newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull MeasurementEntity oldItem, @NonNull MeasurementEntity newItem) {
            return oldItem.getTime() == newItem.getTime();
        }
    }
}
