import java.util.ArrayList;
import java.util.List;

public class Journal {
    private List<String> logs = new ArrayList<>();

    public void log(String message){
        String entry = "[" + System.currentTimeMillis() + "] " + message;
        logs.add(entry);
        System.out.println(entry);
    }

    public List<String> getLogs() {
        return logs;
    }
}