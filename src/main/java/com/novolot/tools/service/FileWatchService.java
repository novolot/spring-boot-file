package com.novolot.tools.service;

import com.novolot.tools.domain.WatchedFile;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class FileWatchService<T extends WatchedFile>  {

    private final Set<T> files = new HashSet<>();
    private final File path;
    private final Pattern pattern;
    private final Map<String, T> data = new HashMap<>();

    public List<T> getAllFiles() {
        return new ArrayList<>(files);
    }


    protected abstract T createFileEntity(File file);

    public T get(String key) {
        if (this.data.containsKey(key) && this.data.get(key).isExists())
            return this.data.get(key);
        return null;
    }

    @Scheduled(fixedDelayString = "${filewatchservice.timeout:30000}")
    @PostConstruct
    public void update() throws FileNotFoundException {
        if (!this.path.exists())
            throw new FileNotFoundException(String.format("Path %s not found", this.path.getAbsolutePath()));
        if (!this.path.isDirectory())
            throw new FileNotFoundException(String.format("Path %s is not directory", this.path.getAbsolutePath()));
        update(this.path);
    }

    private synchronized void update(File dir) {
        List<File> filesList = Arrays.asList(dir.listFiles());
        filesList.sort(Comparator.comparing(File::getName));
        for (File file : filesList) {
            if (file.isDirectory()) {
                update(file);
            }
            String name = file.getAbsolutePath().replace(path.getAbsolutePath() + "/", "");
            Matcher matcher = pattern.matcher(name);
            if (!matcher.matches())
                continue;
            T entry = createFileEntity(file);
            if (files.contains(entry))
                continue;

            files.add(entry);
            if (matcher.groupCount() > 1) {
                this.data.put(entry.setKey(matcher.group(1)), entry);
            }
        }
        for (WatchedFile entry : new ArrayList<>(files)) {
            entry.watch();
            if (!entry.isExists()) {
                this.files.remove(entry);
                this.data.remove(entry.getKey());
            }
        }
    }

    public FileWatchService(String path, String pattern) {
        this.path = new File(path);
        this.pattern = Pattern.compile(pattern);
    }

}
