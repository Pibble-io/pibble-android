package com.star.pibbledev.home.createmedia.mediapicker.ui.picker.models;


import java.util.Arrays;

public class Photo extends BaseFile {

    public Photo(int id, String name, String path, String mime_type, long duration, long date) {
        super(id, name, path, mime_type, duration, date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;

        Photo photo = (Photo) o;

        return id == photo.id;
    }

    @Override
    public int hashCode() {
        return hash(id, path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mime_type;
    }

    public void setMimeType(String mimeType) {
        this.mime_type = mimeType;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    private static int hash(Object... values) {
        return Arrays.hashCode(values);
    }
}
