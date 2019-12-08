package cs213.android56;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class PhotoActivity extends AppCompatActivity {
    ListView tags;
    Tag selectedTag;
    List<Tag> tagList;
    List<Album> albumList;
    ImageView photo;
    String albumname;
    Photo current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
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
        String photoname = bundle.getString("photoname");
        for(Album a: albumList) {
            if(a.getName().equals(albumname)) {
                for(Photo p: a.getPhotos()) {
                    if(p.getFilename().equals(photoname)) {
                        current = p;
                        break;
                    }
                }
                break;
            }
        }

        tagList = current.getTags();
        tags = findViewById(R.id.phototagslist);
        tags.setAdapter(new ArrayAdapter<Tag>(this, android.R.layout.simple_list_item_1, tagList));

        tags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?   > parent, View view, int position, long id) {
                selectedTag = (Tag) tags.getItemAtPosition(position);
            }
        });

        photo = findViewById(R.id.photoimage);
        photo.setImageBitmap(BitmapFactory.decodeFile(photoname));


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
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addtag) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add tag of type:");
            final Spinner tagtypes = new Spinner(this);
            List<String> types = new ArrayList<String>();
            types.add("Person");
            types.add("Location");
            tagtypes.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types));
            builder.setView(tagtypes);

            builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(tagtypes.getSelectedItem() == null) {
                        dialog.cancel();
                        return;
                    }
                    if(tagtypes.toString().equals("Person"))
                        addTagPerson();
                    else
                        addTagLocation();
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


        if (id == R.id.action_removetag) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Tag");

            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteTag(selectedTag);
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

        if (id == R.id.action_slideshow) {
            Bundle bundle = new Bundle();
            bundle.putString("albumname", albumname);
            Intent intent = new Intent(this, SlideshowActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteTag(Tag t) {
        tagList.remove(t);
        tags.setAdapter(new ArrayAdapter<Tag>(this, android.R.layout.simple_list_item_1, tagList));
    }

    public void addTagPerson() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add tag");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText() == null) {
                    dialog.cancel();
                    return;
                }
                addTagPerson2(input.getText().toString());
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

    public void addTagLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add tag");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText() == null) {
                    dialog.cancel();
                    return;
                }
                addTagLocation2(input.getText().toString());
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

    public void addTagPerson2(String value) {
        Tag t = new Tag("Person", value);

        for(Tag tag: tagList) {
            if(tag.equals(t)) {
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("Tag invalid");
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

        tagList.add(t);
        tags.setAdapter(new ArrayAdapter<Tag>(this, android.R.layout.simple_list_item_1, tagList));
    }

    public void addTagLocation2(String value) {
        Tag t = new Tag("Location", value);

        for(Tag tag: tagList) {
            if(tag.equals(t)) {
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("Tag invalid");
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

        tagList.add(t);
        tags.setAdapter(new ArrayAdapter<Tag>(this, android.R.layout.simple_list_item_1, tagList));
    }

}
