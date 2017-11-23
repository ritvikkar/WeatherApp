package com.ritvikkar.weatherapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ritvikkar.weatherapp.R;
import com.ritvikkar.weatherapp.WeatherActivity;
import com.ritvikkar.weatherapp.data.Location;

import java.util.ArrayList;
import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivIcon;
        public TextView tvName;
        public TextView tvCurrentTemperature;
        public TextView tvDescription;
        public Button btnDetails;
        public Button btnDelete;

        public ViewHolder(View weatherView) {
            super(weatherView);
            ivIcon = weatherView.findViewById(R.id.ivWeatherIcon);
            tvName = weatherView.findViewById(R.id.tvName);
            tvCurrentTemperature = weatherView.findViewById(R.id.tvCurrentTemperature);
            tvDescription = weatherView.findViewById(R.id.tvDescription);
            btnDetails = weatherView.findViewById(R.id.btnDetails);
            btnDelete = weatherView.findViewById(R.id.btnDelete);
        }
    }

    private List<Location> locationList;
    private Context context;

    public WeatherAdapter(Context context) {
        this.locationList = new ArrayList<>();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Location location = locationList.get(position);
        holder.tvName.setText(location.getCity());
        holder.tvDescription.setText(location.getDescription());
        holder.tvCurrentTemperature.setText(
                String.format(context.getString(R.string.txt_temp_cel),
                        Double.toString(location.getTemp())));

        String url = context.getString(R.string.txt_url_img_base) + location.getIcon() + ".png";
        Glide.with(context).load(url).centerCrop().into(holder.ivIcon);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });

        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((WeatherActivity) context)
                        .showDetailsLocationActivity
                                (holder.tvName.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public void addLocation(Location location) {
        locationList.add(location);
        notifyDataSetChanged();
    }

    public Location getItem(int i) {
        return locationList.get(i);
    }
}
