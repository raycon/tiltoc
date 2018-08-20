package com.raegon.til;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class Readme {

    public static String print(Directory directory) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# TIL\n");
        lines.add("> Today I Learned\n");

        // Recent Modified
        lines.add("## Recently Modified\n");
        List<Path> modifiedDocuments = directory.getRecentModifiedDocuments(15);
        for (Path modified : modifiedDocuments) {
            FileTime time = Files.getLastModifiedTime(modified, LinkOption.NOFOLLOW_LINKS);
            lines.add("- *" + time.toString().substring(0, 10) + "* : " + getAnchor(modified.getParent(), modified));
        }
        lines.add("\n----\n");

        // Category
        lines.add("## Categories\n");
        lines.add("Total `" + directory.getDocumentCount() + "` TILs\n");
        List<Path> categories = directory.getCategories();
        for (Path category : categories) {
            lines.add("- " + getAnchor(category) + " *(" + directory.getDocuments(category).size() + ")*");
        }
        lines.add("\n----\n");

        // Documents
        for (Path category : categories) {
            List<Path> documents = directory.getDocuments(category);
            lines.add("### " + category.getFileName().toString() + "\n");
            for (Path document : documents) {
                lines.add("- " + getAnchor(category, document));
            }
            lines.add("");
        }
        return String.join(System.lineSeparator(), lines);
    }

    private static String getAnchor(Path category) {
        String name = category.getFileName().toString();
        return getAnchor(name, "#" + name);
    }

    private static String getAnchor(Path category, Path document) {
        String categoryName = category.getFileName().toString();
        String fileName = document.getFileName().toString();
        String title = getDocumentName(document);
        return getAnchor(title, categoryName + "/" + fileName);
    }

    private static String getAnchor(String title, String link) {
        return "[" + title + "](" + link + ")";
    }

    private static String getDocumentName(Path document) {
        String name = document.getFileName().toString().split("\\.")[0];
        return Arrays.stream(name.split("-"))
                .map(Readme::capitalize)
                .collect(Collectors.joining(" "));
    }

    private static String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

}
