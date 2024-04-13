package views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.trawa01.R;

public class MeasurementViewHolder extends RecyclerView.ViewHolder{
    private final TextView measurementItemView;

    private MeasurementViewHolder(View itemView) {
        super(itemView);
        measurementItemView = itemView.findViewById(R.id.textView);
    }

    public void bind(String text) {
        measurementItemView.setText(text);
    }

    public static MeasurementViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new MeasurementViewHolder(view);
    }
}
