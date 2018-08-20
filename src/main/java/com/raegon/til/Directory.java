package com.raegon.til;

import lombok.Data;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Directory {

    private final String extension;

    private final Path root;

    private List<String> ignores = new ArrayList<>();

    private List<Path> categories;

    public Directory(Path root, String extension, List<String> ignores) {
        this.root = root;
        this.extension = extension;
        this.ignores.addAll(ignores);
    }

    public List<Path> getCategories() {
        return listPath(root, path -> {
            String filename = path.getFileName().toString();
            return Files.isDirectory(path) && !filename.startsWith(".") && !ignores.contains(filename);
        });
    }

    public List<Path> getDocuments(Path category) {
        return listPath(category, path -> {
            String filename = path.getFileName().toString();
            return filename.endsWith(extension) && !filename.startsWith(".") && !ignores.contains(filename);
        });
    }

    public List<Path> listPath(Path path, DirectoryStream.Filter<Path> filter) {
        try {
            DirectoryStream<Path> paths = Files.newDirectoryStream(path, filter);
            return convert(paths);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<Path> getDocuments() {
        return getCategories().stream().flatMap(c -> getDocuments(c).stream()).collect(Collectors.toList());
    }

    public <T> List<T> convert(DirectoryStream<T> directoryStream) {
        List<T> list = new ArrayList<>();
        directoryStream.iterator().forEachRemaining(list::add);
        return list;
    }

    public int getDocumentCount() {
        return getDocuments().size();
    }

    public List<Path> getRecentModifiedDocuments(int count) {
        return getDocuments().stream().sorted((p1, p2) -> {
            try {
                FileTime t1 = Files.getLastModifiedTime(p1, LinkOption.NOFOLLOW_LINKS);
                FileTime t2 = Files.getLastModifiedTime(p2, LinkOption.NOFOLLOW_LINKS);
                return t2.compareTo(t1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }).limit(count).collect(Collectors.toList());
    }

}
