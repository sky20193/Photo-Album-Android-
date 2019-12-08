package cs213.android56;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {
    ListView photos;
    Photo selectedPhoto;
    List<Photo> photoList;
    List<Album> albumList;
    String albumname;
    static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
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

        photos = findViewById(R.id.photolist);
        photos.setAdapter(new PhotoArrayAdapter(this, R.layout.photolistrow, photoList));

        photos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?   > parent, View view, int position, long id) {
                selectedPhoto = (Photo) photos.getItemAtPosition(position);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void onStop() {
        saveData();
        super.onStop();
    }

    protected void onPause() {
        super.onPause();
        saveData();
    }

    public void saveData() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(new File(getFilesDir(), "") + File.separator + "albums.ser")));
            oos.writeObject(albumList);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addphoto) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
            return true;
        }

        if (id == R.id.action_displayphoto) {
            if(selectedPhoto == null)
                return true;
            Bundle bundle = new Bundle();
            bundle.putString("albumname", albumname);
            bundle.putString("photoname", selectedPhoto.getFilename());
            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_removephoto) {
            if(selectedPhoto == null)
                return true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Photo");

            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deletePhoto(selectedPhoto);
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
            return true;
        }

        if (id == R.id.action_movephoto) {
            if(selectedPhoto == null)
                return true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Move Photo to album");
            final Spinner input = new Spinner(this);
            input.setAdapter(new ArrayAdapter<Album>(this, android.R.layout.simple_spinner_item, albumList));
            builder.setView(input);

            builder.setPositiveButton("Move", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(input.getSelectedItem() == null) {
                        dialog.cancel();
                        return;
                    }
                    movePhoto((Album) input.getSelectedItem(), selectedPhoto);

                    dialog.cancel();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deletePhoto(Photo selected) {
        photoList.remove(selected);
        photos.setAdapter(new PhotoArrayAdapter(this, R.layout.photolistrow, photoList));
    }

    public void movePhoto(Album toAlbum, Photo selected) {
        toAlbum.addPhoto(selected);
        photoList.remove(selected);
        photos.setAdapter(new PhotoArrayAdapter(this, R.layout.photolistrow, photoList));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            //Uri uri = data.getData();
            //Photo p = new Photo(uri.toString());
            //photoList.add(p);
            //photos.setAdapter(new PhotoArrayAdapter(this, R.layout.photolistrow, photoList));
            //saveData();

            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            photoList.add(new Photo(picturePath));
            photos.setAdapter(new PhotoArrayAdapter(this, R.layout.photolistrow, photoList));
            saveData();
        }
    }
}
