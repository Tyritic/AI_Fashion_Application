package com.JavaBean;

import android.net.Uri;

public class Outfit {
    private String text;
    private Uri image_cloth;
    private Uri image_trousers;
    private Uri image_shoes;

    public Outfit(String text, Uri image_cloth,Uri image_trousers,Uri image_shoes)  {
        this.text = text;
        this.image_cloth = image_cloth;
        this.image_trousers = image_trousers;
        this.image_shoes = image_shoes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Uri getImageCloth() {
        return image_cloth;
    }
    public Uri getImageTrousers() {
        return image_trousers;
    }
    public Uri getImageShoes() {
        return image_shoes;
    }

    public void setImage(Uri image_cloth,Uri image_trousers,Uri image_shoes) {
        this.image_cloth = image_cloth;
        this.image_trousers = image_trousers;
        this.image_shoes = image_shoes;
    }
}
