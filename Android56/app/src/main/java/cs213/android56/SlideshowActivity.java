package cs213.android56;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class SlideshowActivity extends AppCompatActivity {
    List<Album> albumList;
    List<Photo> photoList;
    String albumname;
    int index;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        albumList = new ArrayList<>();
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(new File(getFilesDir(), "") + File.separator + "albums.ser")));
            albumList = (List<Album>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle bundle = getIntent().getExtras();
        albumname = bundle.getString("albumname");
        for(Album a: albumList) {
            if(a.getName().equals(albumname)) {
                photoList = a.getPhotos();
                break;
            }
        }

        index = 0;
        iv = findViewById(R.id.photoiv);
        iv.setImageBitmap(BitmapFactory.decodeFile(photoList.get(index).getFilename()));

        Button nextbutton = findViewById(R.id.nextbutton);
        Button prevbutton = findViewById(R.id.previousbutton);

        nextbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nextImage();
            }
        });

        prevbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                prevImage();
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void nextImage() {
        index++;
        if(index >= photoList.size())
            index = 0;

        iv.setImageBitmap(BitmapFactory.decodeFile(photoList.get(index).getFilename()));
    }

    public void prevImage() {
        index--;
        if(index < 0)
            index = photoList.size()-1;

        iv.setImageBitmap(BitmapFactory.decodeFile(photoList.get(index).getFilename()));
    }
}