package cs213.android56;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Album class to hold information relevant to an album including
 * a list of photos and the earliest/latest date of any photos in the album.
 *
 * @author Umang Patel
 * @author Akashkumar Patel
 */
public class Album implements Serializable, Comparable<Album> {
    private static final long serialVersionUID = 1L;
    String name;
    List<Photo> photos;

    public Album(String name){
        this.name = name;
        photos = new ArrayList<Photo>();
    }

    public Album(String n, List<Photo> p) {
        name = n;
        photos = p;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void addPhoto(Photo photo){
        photos.add(photo);
    }

    public void removePhoto(Photo p) {
        photos.remove(p);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalPhotos() {
        return photos.size();
    }

    public String toString() {
        return name;
    }

    public int compareTo(Album a) {
        return name.compareTo(a.getName());
    }
}