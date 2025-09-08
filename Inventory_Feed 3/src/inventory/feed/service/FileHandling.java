package inventory.feed.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

public class FileHandling {
    private static final Logger logger = Logger.getLogger("FileHandling");

    public static void moveFile(Path source, String targetDir) {
        try {
            Files.createDirectories(Paths.get(targetDir)); // ensure dir exists
            Path target = Paths.get(targetDir, source.getFileName().toString());
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Moved file " + source.getFileName() + " → " + targetDir);
        } catch (IOException e) {
            logger.severe("Error moving file " + source.getFileName() + ": " + e.getMessage());
        }
    }
}
