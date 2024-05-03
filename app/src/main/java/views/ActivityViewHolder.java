package views;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.trawa01.R;
import com.example.trawa01.ui.ViewActivityActivity;
import com.example.trawa01.ui.ViewActivityDetailsActivity;

import ViewModel.ActivityViewModel;

public class ActivityViewHolder extends RecyclerView.ViewHolder{
    private final TextView nameItemView;
    private final TextView distanceItemView;
    private final TextView timeItemView;
    private final Button viewMapButton;
    private final Button viewDetailsButton;
    private final Button deleteButton;
    private final ActivityViewModel activityViewModel;
    private long id;
    private ActivityViewHolder(View itemView, ActivityViewModel activityViewModel) {
        super(itemView);
        nameItemView = itemView.findViewById(R.id.name_value);
        distanceItemView = itemView.findViewById(R.id.distance_value);
        timeItemView = itemView.findViewById(R.id.time_value);
        viewMapButton = itemView.findViewById(R.id.view_map);
        viewDetailsButton = itemView.findViewById(R.id.view_details);
        deleteButton = itemView.findViewById(R.id.delete);
        this.activityViewModel = activityViewModel;
    }

    public void bind(String name, String distance, String time, long id) {
        nameItemView.setText(name);
        distanceItemView.setText(distance);
        timeItemView.setText(time);
        this.id = id;

        viewMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {;
                Intent intent = new Intent(view.getContext(), ViewActivityActivity.class);
                intent.putExtra("id", id);
                view.getContext().startActivity(intent);
            }
        });

        viewDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ViewActivityDetailsActivity.class);
                intent.putExtra("id", id);
                view.getContext().startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityViewModel.deleteActivity(id);
            }
        });


    }

    public static ActivityViewHolder create(ViewGroup parent, ActivityViewModel activityViewModel) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new ActivityViewHolder(view, activityViewModel);
    }
}
