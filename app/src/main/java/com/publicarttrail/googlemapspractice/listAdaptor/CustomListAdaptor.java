package com.publicarttrail.googlemapspractice.listAdaptor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.publicarttrail.googlemapspractice.InfoPage;
import com.publicarttrail.googlemapspractice.R;
import com.publicarttrail.googlemapspractice.events.ArtworkAcquiredEvent;
import com.publicarttrail.googlemapspractice.pojo.Artwork;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;

public class CustomListAdaptor extends RecyclerView.Adapter<ViewHolder> {

    public ArrayList<Artwork> artworks;
    public Context context;

    public CustomListAdaptor(ArrayList<Artwork> artworks, Context context){
        this.artworks = artworks;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Artwork artwork = artworks.get(position);
        holder.title.setText(artwork.getName());
        holder.artist.setText(artwork.getCreator());
        holder.imageView.setImageBitmap(artwork.getBitmap());
        holder.next.setOnClickListener(v -> {
            EventBus.getDefault()
                    .postSticky(new ArtworkAcquiredEvent(Collections.singletonList(artwork)));

            Intent info = new Intent(context, InfoPage.class);
            context.startActivity(info);
        });
    }

    @Override
    public int getItemCount() {
        return artworks.size();
    }
}
