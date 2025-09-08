package inventory.feed.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import inventory.feed.db.DBConnection;

public class InventoryService {
    private static final Logger logger = Logger.getLogger("InventoryService");

    public void processFile(Path filePath, String processedDir, String errorDir) {
        logger.info("Processing file: " + filePath.getFileName());

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            try (BufferedReader reader = Files.newBufferedReader(filePath);
                 PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO inventory (product_name, quantity, price) VALUES (?, ?, ?)")) {

                String line;
                boolean firstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (firstLine) { // Skip header
                        firstLine = false;
                        continue;
                    }

                    String[] parts = line.split(",");
                    try {
                        stmt.setString(1, parts[0].trim());
                        stmt.setInt(2, Integer.parseInt(parts[1].trim()));
                        stmt.setDouble(3, Double.parseDouble(parts[2].trim()));
                        stmt.executeUpdate();
                    } catch (Exception ex) {
                        logger.severe("Error inserting record: " + line + " -> " + ex.getMessage());
                        conn.rollback();
                        FileHandling.moveFile(filePath, errorDir);
                        return; // stop processing this file
                    }
                }

                conn.commit();
                FileHandling.moveFile(filePath, processedDir);
                logger.info("File processed successfully: " + filePath.getFileName());

            } catch (IOException ex) {
                conn.rollback();
                logger.severe("IO Error while reading " + filePath.getFileName() + ": " + ex.getMessage());
                FileHandling.moveFile(filePath, errorDir);
            }

        } catch (SQLException e) {
            logger.severe("Database error while processing " + filePath.getFileName() + ": " + e.getMessage());
            FileHandling.moveFile(filePath, errorDir);
        }
    }
}
