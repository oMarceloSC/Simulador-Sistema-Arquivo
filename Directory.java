import java.util.*;

public class Directory {
    private final String name;
    private final Directory parent;
    private final Map<String, File> files = new HashMap<>();
    private final Map<String, Directory> subdirectories = new HashMap<>();

    public Directory(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public Directory getParent() {
        return parent;
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
        subdirectories.put(directory.getName(), directory);  // <- ESSA LINHA É ESSENCIAL
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

    public String getPath() {
        if (parent == null) return "/";
        return parent.getPath() + name + "/";
    }
}
