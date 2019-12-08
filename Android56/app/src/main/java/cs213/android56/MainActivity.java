package cs213.android56;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView albums;
    Album selectedAlbum;
    List<Album> albumList;
    Context c;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        albumList = new ArrayList<>();
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(new File(getFilesDir(), "") + File.separator + "albums.ser")));
            albumList = (List<Album>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        albums = findViewById(R.id.albumlist);

        albums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?   > parent, View view, int position, long id) {
                selectedAlbum = (Album) albums.getItemAtPosition(position);
            }
        });

        boolean found = false;
        for(Album a: albumList) {
            if(a.getName().equalsIgnoreCase("stock")) {
                found = true;
                break;
            }
        }

        if(!found) {
            //add stock album with stock photos
            Album stockalbum = new Album("stock");
            try {
                InputStream is = getResources().openRawResource(R.raw.cactus);
                FileOutputStream os = new FileOutputStream(new File(getFilesDir() + "/cactus.jpg"));
                int read;
                while ((read = is.read()) != -1)
                    os.write(read);
            } catch (Exception e) {
                e.printStackTrace();
            }
            stockalbum.addPhoto(new Photo(getFilesDir()+"/cactus.jpg"));

            try {
                InputStream is = getResources().openRawResource(R.raw.fire);
                FileOutputStream os = new FileOutputStream(new File(getFilesDir() + "/fire.png"));
                int read;
                while ((read = is.read()) != -1)
                    os.write(read);
            } catch (Exception e) {
                e.printStackTrace();
            }
            stockalbum.addPhoto(new Photo(getFilesDir()+"/fire.png"));

            try {
                InputStream is = getResources().openRawResource(R.raw.garagedoor);
                FileOutputStream os = new FileOutputStream(new File(getFilesDir() + "/garagedoor.jpg"));
                int read;
                while ((read = is.read()) != -1)
                    os.write(read);
            } catch (Exception e) {
                e.printStackTrace();
            }
            stockalbum.addPhoto(new Photo(getFilesDir()+"/garagedoor.jpg"));

            try {
                InputStream is = getResources().openRawResource(R.raw.grass);
                FileOutputStream os = new FileOutputStream(new File(getFilesDir() + "/grass.jpg"));
                int read;
                while ((read = is.read()) != -1)
                    os.write(read);
            } catch (Exception e) {
                e.printStackTrace();
            }
            stockalbum.addPhoto(new Photo(getFilesDir()+"/grass.jpg"));

            try {
                InputStream is = getResources().openRawResource(R.raw.leaves);
                FileOutputStream os = new FileOutputStream(new File(getFilesDir() + "/leaves.jpg"));
                int read;
                while ((read = is.read()) != -1)
                    os.write(read);
            } catch (Exception e) {
                e.printStackTrace();
            }
            stockalbum.addPhoto(new Photo(getFilesDir()+"/leaves.jpg"));

            albumList.add(stockalbum);
        }

        albums.setAdapter(new ArrayAdapter<Album>(this, android.R.layout.simple_list_item_1, albumList));

        c = this;
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setTitle("Create new Album");
                final EditText input = new EditText(c);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(input.getText() == null) {
                            dialog.cancel();
                            return;
                        }
                        createAlbum(input.getText().toString());
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
            }
        });
    }

    protected void onPause() {
        super.onPause();
        saveData();
    }

    protected void onStop() {
        saveData();
        super.onStop();
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_rename) {
            if(selectedAlbum == null)
                return true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Rename Album");
            final EditText input = new EditText(this);
            input.setText(selectedAlbum.getName());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(input.getText() == null) {
                        dialog.cancel();
                        return;
                    }
                    renameAlbum(input.getText().toString(), selectedAlbum);

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

        if (id == R.id.action_delete) {
            if(selectedAlbum == null)
                return true;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Album "+selectedAlbum.getName());

            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteAlbum(selectedAlbum);
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

        if (id == R.id.action_open) {
            if(selectedAlbum == null)
                return true;
            saveData();
            openAlbum(selectedAlbum.getName());
            return true;
        }

        if (id == R.id.action_search) {
            saveData();
            startSearch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createAlbum(String newname) {
        for(Album a: albumList) {
            if(a.getName().equalsIgnoreCase(newname)) {
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("Album name invalid");
                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                b.show();
                return;
            }
        }

        Album newAlbum = new Album(newname);
        albumList.add(newAlbum);
        albums.setAdapter(new ArrayAdapter<Album>(this, android.R.layout.simple_list_item_1, albumList));
    }

    public void renameAlbum(String newname, Album selected) {
        for(Album a: albumList) {
            if(a.getName().equalsIgnoreCase(newname)) {
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("Album name invalid");
                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                b.show();
                return;
            }
        }

        selected.setName(newname);
        albums.setAdapter(new ArrayAdapter<Album>(this, android.R.layout.simple_list_item_1, albumList));
    }

    public void deleteAlbum(Album selected) {
        albumList.remove(selected);
        albums.setAdapter(new ArrayAdapter<Album>(this, android.R.layout.simple_list_item_1, albumList));
    }

    public void openAlbum(String albumname) {
        Bundle bundle = new Bundle();
        bundle.putString("albumname", albumname);
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void startSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
}
