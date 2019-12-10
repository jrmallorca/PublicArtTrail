package com.publicarttrail.googlemapspractice;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.Map;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;
    private Map<Marker,Integer> markerAndImage;

    public CustomInfoWindowAdapter(Activity context, Map<Marker,Integer> markerAndImage){
        this.context = context;
        this.markerAndImage = markerAndImage;
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
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvSubTitle = (TextView) view.findViewById(R.id.tv_subtitle);
        ImageView im = (ImageView) view.findViewById(R.id.imageView1);

        //set text and image
        tvTitle.setText(marker.getTitle());
        tvSubTitle.setText(marker.getSnippet());
         im.setImageResource(markerAndImage.get(marker));


        return view;
    }
}