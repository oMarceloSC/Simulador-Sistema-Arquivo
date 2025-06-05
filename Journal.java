import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Journal {
    private final String log = "journal.log";

    public void log(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(log, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.write("[" + timestamp +  "] " + message + "\n");
        } catch (IOException e) {
            System.err.println("Error writing to journal.");
        }
    }
}