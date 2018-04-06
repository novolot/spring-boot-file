package com.novolot.tools;

import com.novolot.tools.service.FileWatchService;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class TestFileService extends FileWatchService<TestFile> {

    public TestFileService() {
        super("./examples/", "(.*)(\\.js|\\.txt)$");
    }

    @Override
    protected TestFile createFileEntity(File file) {
        return new TestFile(file);
    }
}
