package com.publicarttrail.googlemapspractice;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.publicarttrail.googlemapspractice.pojo.Trail;

import java.util.List;
import java.util.Objects;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;
    private List<Trail> trails;

    public CustomInfoWindowAdapter(Activity context, List<Trail> trails){
        this.context = context;
        this.trails = trails;
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
        for (Trail t : trails) {
            if (t.getArtworkMap().containsKey(marker)) {
                im.setImageBitmap(Objects.requireNonNull(t.getArtworkMap().get(marker)).getBitmap());
            }
        }
        return view;
    }
}