package cs213.android56;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

public class PhotoArrayAdapter extends ArrayAdapter<Photo> {
    private final Context context;
    private final List<Photo> values;
    private int resourceID;

    public PhotoArrayAdapter(Context context, int resourceID, List<Photo> values) {
        super(context, resourceID, values);
        this.context = context;
        this.resourceID = resourceID;
        this.values = values;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceID, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.photoname);
            holder.imageView = (ImageView) convertView.findViewById(R.id.photoimage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Photo rowItem = (Photo) values.get(position);
        holder.textView.setText(rowItem.getCaption());

        holder.imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(rowItem.getFilename()), 100,100));
        //holder.imageView.setAdjustViewBounds(true);
        //holder.imageView.setMaxHeight(100);
        //holder.imageView.setMaxWidth(100);
        //holder.imageView.setImageURI(Uri.parse(rowItem.getFilename()));

        return convertView;
    }

}