package com.raegon.til;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Application {

    private static final String OPT_DIR = "d";

    private static final String OPT_HELP = "h";

    private static final String OPT_IGNORE = "i";

    private static final String OPT_EXTENSION = "e";

    private static final String OPT_OUTPUT = "o";

    public static void main(String[] args) throws ParseException, IOException {
        // Options
        Options options = new Options();
        options.addOption(OPT_DIR, true, "directory. default: current working directory");
        options.addOption(OPT_HELP, false, "print this message");
        Option option = new Option(OPT_IGNORE, "ignore files. default: drafts");
        option.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(OPT_EXTENSION, true, "extention. default: md");
        options.addOption(OPT_OUTPUT, true, "output file. default: README.md");
        options.addOption(option);

        // Parse
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        // Help
        if (cmd.hasOption(OPT_HELP)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar tiltoc.jar -o README.md", options);
            return;
        }

        // Execute
        String dir = System.getProperty("user.dir");
        if (cmd.hasOption(OPT_DIR)) {
            dir = cmd.getOptionValue(OPT_DIR);
        }

        String output = cmd.getOptionValue(OPT_OUTPUT, "README.md");

        List<String> ignores = new ArrayList<>();
        if (cmd.hasOption(OPT_IGNORE)) {
            ignores = Arrays.asList(cmd.getOptionValues(OPT_IGNORE));
        } else {
            ignores.add("drafts");
        }

        Path root = Paths.get(dir);
        Directory tp = new Directory(root, cmd.getOptionValue(OPT_EXTENSION, "md"), ignores);
        String readme = Readme.print(tp);
        Files.write(root.resolve(output), readme.getBytes());
    }

}
