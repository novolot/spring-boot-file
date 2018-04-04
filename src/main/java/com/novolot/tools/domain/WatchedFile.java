package com.novolot.tools.domain;

import java.io.File;

public abstract class WatchedFile {

    final File file;
    long lastModified = -1;

    public abstract void update();
    public abstract void delete();

    public File getFile() {return file;}

    public boolean isChanged() {
        return this.file.lastModified() != lastModified;
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
