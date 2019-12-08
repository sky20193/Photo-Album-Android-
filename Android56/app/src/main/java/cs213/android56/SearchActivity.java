package cs213.android56;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    ListView photos;
    List<Photo> photoList;
    List<Album> albumList;
    Spinner tagspinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        albumList = new ArrayList<>();
        photoList = new ArrayList<>();
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(new File(getFilesDir(), "") + File.separator + "albums.ser")));
            albumList = (List<Album>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(Album a: albumList) {
            for(Photo p: a.getPhotos())
                photoList.add(p);
        }

        photos = findViewById(R.id.searchresultlist);
        photos.setAdapter(new PhotoArrayAdapter(this, R.layout.photolistrow, photoList));

        tagspinner = findViewById(R.id.tagtypespinner);
        List<String> tagtypes = new ArrayList<>();
        tagtypes.add("Person");
        tagtypes.add("Location");
        tagspinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tagtypes));

        Button searchbutton = findViewById(R.id.searchbutton);
        searchbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void search() {
        TextInputEditText text = findViewById(R.id.searchtext);
        if(text.getText().toString() == null)
            return;

        List<Photo> searchlist = new ArrayList<>();
        String type = tagspinner.getSelectedItem().toString();

        for(Photo p: photoList) {
            for(Tag t: p.getTags()) {
                if(t.getType().equals(type)) {
                    if(t.getValue().equalsIgnoreCase(text.getText().toString())) {
                        searchlist.add(p);
                        break;
                    }
                }
            }
        }

        photos.setAdapter(new PhotoArrayAdapter(this, R.layout.photolistrow, searchlist));
    }

}
