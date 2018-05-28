package com.novolot.tools;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestApplication.class})
@ActiveProfiles({"test"})
@TestPropertySource(properties = "debug=true")
public class FileTest {

    @Autowired
    TestFileService service;

    @BeforeEach
    @AfterEach
    public void before() throws IOException {
        if (Files.exists(Paths.get("./examples/4.txt")))
            Files.delete(Paths.get("./examples/4.txt"));
    }

    @Test
    public void testLoad() {
        assertEquals(service.getAllFiles().size(), 2);
    }

    @Test
    public void testMap() {
        assertEquals(service.get("2").getString(), "hello world\n");
    }

    @Test
    public void testUpdate() throws IOException, InterruptedException {

        Path p = Files.createFile(Paths.get("./examples/4.txt"));
        Files.write(Paths.get("./examples/4.txt"), Arrays.asList("1"), Charset.defaultCharset());
        service.update();
        assertEquals(service.getAllFiles().size(), 3);
        assertEquals(service.get("4").getString(), "1\n");
        TimeUnit.SECONDS.sleep(2);
        Files.write(Paths.get("./examples/4.txt"), Arrays.asList("2"), Charset.defaultCharset());
        service.update();
        assertEquals(service.get("4").getString(), "2\n");
        Files.delete(Paths.get("./examples/4.txt"));
        assertEquals(service.get("4"), null);
        service.update();
        assertEquals(service.getAllFiles().size(), 2);
    }
}
