package cs213.android56;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Class to hold Photo data including location(filename) of photo,
 * caption, tags, and date
 *
 * @author Umang Patel
 * @author Akashkumar Patel
 */
public class Photo implements Serializable, Comparable<Photo> {

    private static final long serialVersionUID = 1L;
    String filename;
    String caption;
    List<Tag> tags;

    public Photo(String file) {
        filename = file;
        tags = new ArrayList<Tag>();
        caption = filename;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String c) {
        caption = c;
    }

    public void addTag(Tag t) {
        tags.add(t);
    }

    public void deleteTag(Tag t) {
        tags.remove(t);
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getFilename() {
        return filename;
    }

    public int compareTo(Photo p) {
        return filename.compareTo(p.getFilename());
    }
}