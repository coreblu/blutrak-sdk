package com.averos.blutrak.sample.List;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.averos.blutrak.sample.R;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.attention.ShakeAnimator;

import java.util.List;

/**
 * Created by hassan on 5/21/2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    public interface ListInteractionListener {
        void deleteTag(String mac, String name);

        void startRinging(String mac, String name);

        void stopRinging(String mac, String name);

        void sleepTag(String mac, String name);

        void showInfo(String mac, String name);
    }

    private List<ListModel> values;
    private ListInteractionListener listInteractionListener;


    public ListAdapter(List<ListModel> values) {
        this.values = values;
    }

    public void update(List<ListModel> values) {
        this.values = values;
        notifyDataSetChanged();
    }

    public void setListInteractionListener(ListInteractionListener listInteractionListener) {
        this.listInteractionListener = listInteractionListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_blutrak, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListModel model = values.get(position);
        holder.name.setText(model.getName());
        holder.mac.setText(model.getMac());

        if (model.isPendingAlert()) {
            YoYo.with(new ShakeAnimator()).duration(500).repeat(6).playOn(holder.rootView);
            model.setPendingAlert(false);
        }

        switch (model.getConnectionState()) {
            case Connected:
                holder.status.setText("Connected");
                holder.status.setTextColor(Color.GREEN);
                break;
            case Disconnected:
                holder.status.setText("Disconnected");
                holder.status.setTextColor(Color.RED);
                break;
            case Connecting:
                holder.status.setText("Connecting");
                holder.status.setTextColor(Color.YELLOW);
                break;
            case Disconnecting:
                holder.status.setText("Disconnecting");
                holder.status.setTextColor(Color.YELLOW);
                break;
        }


    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View rootView;
        public TextView name;
        public TextView status;
        public TextView mac;
        public ImageButton ring;
        public ImageButton info;
        public ImageButton sleep;
        public ImageButton delete;
        public ImageButton stopRing;

        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.name = (TextView) rootView.findViewById(R.id.name);
            this.status = (TextView) rootView.findViewById(R.id.status);
            this.mac = (TextView) rootView.findViewById(R.id.mac);
            this.ring = (ImageButton) rootView.findViewById(R.id.ring);
            this.info = (ImageButton) rootView.findViewById(R.id.info);
            this.sleep = (ImageButton) rootView.findViewById(R.id.sleep);
            this.delete = (ImageButton) rootView.findViewById(R.id.delete);
            this.stopRing = (ImageButton) rootView.findViewById(R.id.stop_ring);

            sleep.setOnClickListener(this);
            ring.setOnClickListener(this);
            info.setOnClickListener(this);
            delete.setOnClickListener(this);
            stopRing.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listInteractionListener == null)
                return;
            final ListModel model = values.get(getAdapterPosition());
            switch (view.getId()) {
                case R.id.ring:
                    listInteractionListener.startRinging(model.getMac(), model.getName());
                    break;
                case R.id.sleep:
                    listInteractionListener.sleepTag(model.getMac(), model.getName());
                    break;
                case R.id.delete:
                    listInteractionListener.deleteTag(model.getMac(), model.getName());
                    break;
                case R.id.info:
                    listInteractionListener.showInfo(model.getMac(), model.getName());
                    break;
                case R.id.stop_ring:
                    listInteractionListener.stopRinging(model.getMac(), model.getName());
                    break;
            }
        }
    }
}

