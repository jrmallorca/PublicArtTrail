package com.publicarttrail.googlemapspractice;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.publicarttrail.googlemapspractice.pojo.Artwork;

import java.util.Map;
import java.util.Objects;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;
    private Map<Marker, Artwork> markerArtwork;

    public CustomInfoWindowAdapter(Activity context, Map<Marker, Artwork> markerArtwork){
        this.context = context;
        this.markerArtwork = markerArtwork;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.customwindow, null);

        //use xml for customwindow : consists of textview for title and subtitle (name and author respectively)
        //also consists of picture that is retrieved using drawableId attribute of artwork

        //adjust individual formats according to xml
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvSubTitle = view.findViewById(R.id.tv_subtitle);
        ImageView im = view.findViewById(R.id.imageView1);

        //set text and image
        tvTitle.setText(marker.getTitle());
        tvSubTitle.setText(marker.getSnippet());
        im.setImageBitmap(Objects.requireNonNull(markerArtwork.get(marker)).getBitmap());
        return view;
    }
}