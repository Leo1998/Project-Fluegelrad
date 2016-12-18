package de.projectfluegelrad.database;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ImageAtlas implements Parcelable {

    public static final Creator<ImageAtlas> CREATOR = new Creator<ImageAtlas>() {
        @Override
        public ImageAtlas createFromParcel(Parcel in) {
            return new ImageAtlas(in);
        }

        @Override
        public ImageAtlas[] newArray(int size) {
            return new ImageAtlas[size];
        }
    };

    protected ImageAtlas(Parcel in) {
        this.images = in.readArrayList(this.getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(images);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private ArrayList<Image> images;

    public ImageAtlas() {
        this.images = new ArrayList<>();
    }

    public void addImage(Image image) {
        images.add(image);
    }

    public ArrayList<Image> getAllImages() {
        return images;
    }

    public ArrayList<Image> getImages(Event event) {
        ArrayList<Image> list = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);

            if (image.getEventId() == event.getId()) {
                list.add(image);
            }
        }

        return list;
    }

    public void clearImages() {
        for (Image img : images) {
            img.disposeBitmap();
        }

        images.clear();
    }
}
