package de.projectfluegelrad.database;

import java.util.ArrayList;

public class ImageAtlas {

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

}
