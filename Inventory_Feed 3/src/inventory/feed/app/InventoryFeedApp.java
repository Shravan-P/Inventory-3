package inventory.feed.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import inventory.feed.service.InventoryService;

public class InventoryFeedApp {
    private static final Logger logger = Logger.getLogger("InventoryFeedApp");

    private static final String INPUT_DIR = "D:\\litmus7\\Inventory_Feed 3\\src\\files\\input file";
    private static final String PROCESSED_DIR ="D:\\litmus7\\Inventory_Feed 3\\src\\files\\processed files";
    private static final String ERROR_DIR = "D:\\litmus7\\Inventory_Feed 3\\src\\files\\error files";

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5); // limit to 5 threads

        try {
            // Ensure directories exist
            Files.createDirectories(Paths.get(PROCESSED_DIR));
            Files.createDirectories(Paths.get(ERROR_DIR));

            List<Path> fileList = new ArrayList<>(Files.list(Paths.get(INPUT_DIR)).toList());

            // Submit each file as a task
            for (Path file : fileList) {
                executor.submit(() -> {
                    InventoryService service = new InventoryService();
                    service.processFile(file, PROCESSED_DIR, ERROR_DIR);
                });
                logger.info("Task submitted for file: " + file.getFileName());
            }

        } catch (IOException e) {
            logger.severe("Error scanning input folder: " + e.getMessage());
        } finally {
            executor.shutdown(); // stop accepting new tasks
            try {
                if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                    logger.warning("Execution did not end ");
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                logger.severe("Executor interrupted: " + e.getMessage());
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
