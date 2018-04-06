package com.novolot.tools.domain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class WatchedFile {

    final File file;
    long lastModified = -1;
    String key = null;

    String fileString = null;

    public String setKey(String key) {
        this.key = key;
        return key;
    }

    public String getKey() {
        return this.key;
    }



    public abstract void update();
    public abstract void delete();

    public File getFile() {return file;}


    public String getString(String encoding) {
        if (fileString != null) return fileString;
        try {
            byte[] data = Files.readAllBytes(this.file.toPath());
            fileString = new String(data, encoding);
        }catch (IOException ie) {
            fileString = null;
        }
        return fileString;
    }

    public String getString() {
        return getString("UTF-8");
    }


    public boolean isChanged() {

        return new File(this.file.getAbsolutePath()).lastModified() != lastModified;
    }


    public boolean isExists() {
        return this.file.exists() && this.file.isFile();
    }

    public void watch() {
        if (!this.isExists()){
            this.delete();
            return;
        }

        if (this.isChanged()) {
            this.update();
            this.fileString = null;
            this.lastModified = this.file.lastModified();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj instanceof WatchedFile)) {
            WatchedFile fileEntity = (WatchedFile) obj;
            return file != null
                    && fileEntity.getFile() != null
                    && file.getAbsolutePath().equals(fileEntity.getFile().getAbsolutePath());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return file.getAbsolutePath().hashCode();
    }

    public WatchedFile(File file) {
        this.file = file;
    }

}
