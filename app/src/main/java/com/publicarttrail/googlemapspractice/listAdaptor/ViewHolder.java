package com.publicarttrail.googlemapspractice.listAdaptor;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.publicarttrail.googlemapspractice.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public TextView title;
    public TextView artist;
    public ImageView next;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        this.imageView = (ImageView) itemView.findViewById(R.id.artpic);
        this.title = (TextView) itemView.findViewById(R.id.artname);
        this.artist = (TextView) itemView.findViewById(R.id.artartist);
        this.next = (ImageView) itemView.findViewById(R.id.nextArrow);
    }
}
