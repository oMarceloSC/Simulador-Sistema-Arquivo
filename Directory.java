import java.util.*;

public class Directory {
    private String name;
    private Map<String, File> files = new HashMap<>();
    private Map<String, Directory> subdirectories = new HashMap<>();

    public Directory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<String, File> getFiles() {
        return files;
    }

    public Map<String, Directory> getSubdirectories() {
        return subdirectories;
    }

    public void addFile(File file) {
        files.put(file.getName(), file);
    }

    public void addSubdirectory(Directory directory) {
        subdirectories.put(directory.getName(), directory);
    }

    public void removeFile(String fileName) {
        files.remove(fileName);
    }

    public void removeSubdirectory(String directoryName) {
        subdirectories.remove(directoryName);
    }

    public File getFile(String fileName) {
        return files.get(fileName);
    }

    public Directory getSubdirectory(String directoryName) {
        return subdirectories.get(directoryName);
    }
}
