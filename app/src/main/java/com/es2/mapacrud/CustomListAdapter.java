package com.es2.mapacrud;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.es2.mapacrud.database.models.Photo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class CustomListAdapter extends ArrayAdapter<Photo> {
    private final Activity context;
    private Photo[] photos;

    public CustomListAdapter(Activity context, Photo[] photos) {
        super(context, R.layout.mylist, photos);
        this.context=context;
        this.photos=photos;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        try {
            final int THUMBNAIL_SIZE = 64;

            FileInputStream fis = new FileInputStream(this.photos[position].getPath());
            Bitmap imageBitmap = BitmapFactory.decodeStream(fis);
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            imageView.setImageBitmap(imageBitmap);
            txtTitle.setText("Imagem " + Long.toString(photos[position].getPhotoId()));
            String dataStr =  "Lat: " + photos[position].getLat() + ", Lng: " + photos[position].getLng() + "\nX: " + photos[position].getX() + ", Y: " + photos[position].getY() + ", Z: " + photos[position].getZ();
            extratxt.setText(dataStr);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return rowView;
    };

}
