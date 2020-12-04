package com.star.pibbledev.home.createmedia.mediapicker.ui.picker.models;

/**
 * Created by droidNinja on 29/07/16.
 */
public class BaseFile {
    protected int id;
    protected String name;
    protected String path;
    protected String mime_type;
    protected long duration;
    protected long date;

    public BaseFile(int id, String name, String path, String mime_type, long duration, long date) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.mime_type = mime_type;
        this.duration = duration;
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseFile)) return false;

        BaseFile baseFile = (BaseFile) o;

        return id == baseFile.id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMimeType() {
        return mime_type;
    }

    public void setMimeType(String mimeType) {
        this.mime_type = mimeType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
