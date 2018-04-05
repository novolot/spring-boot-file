package com.novolot.tools;

import com.novolot.tools.domain.WatchedFile;

import java.io.File;

public class TestFile extends WatchedFile {

    public TestFile(File file) {
        super(file);
    }

    @Override
    public void update() {

    }

    @Override
    public void delete() {

    }
}
