import java.util.*;

public class FileSystemSimulator {
    private final Directory root = new Directory("root", null);
    private final Journal journal = new Journal();
    private final Scanner scanner = new Scanner(System.in);
    private Directory current = root;

    public static void main(String[] args) {
        new FileSystemSimulator().start();
    }

    public void start() {
        System.out.println("Sistem File Simulator");

        while(true) {
            System.out.print(current.getPath() + " > ");
            String input = scanner.nextLine();
            String[] parts = input.split(" ", 2);
            String command = parts[0];
            String arg = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "mkdir" -> createDirectory(arg);
            case "ls" -> listFiles();
            case "cd" -> changeDirectory(arg);
            case "touch" -> createFile(arg);
            case "rm" -> deleteFile(arg);
            case "exit" -> {
                System.out.println("Exiting...");
                return;
            }
            default -> System.out.println("Unknown command: " + command);
            }
        }
    }

    private void createFile(String name) {
        if (name.isEmpty()) return;
        if (current.getFile(name) != null) {
            System.out.println("File already exists!");
            return;
        }

        System.out.print("Content: ");
        String content = scanner.nextLine();
        File newFile = new File(name, content);
        current.addFile(newFile);
        journal.log("File created: " + current.getPath() + name);
    }

    private void listFiles() {
        System.out.println("Directories: ");
        for (String dirName : current.getSubdirectories().keySet())
            System.out.println("  " + dirName);
        
        System.out.println("Files: ");
        for (String fileName : current.getFiles().keySet())
            System.out.println("  " + fileName);
    }

    private void deleteFile(String name) {
        if (current.getFile(name) != null) {
            current.removeFile(name);
            journal.log("File deleted: " + current.getPath() + name);
        } else if (current.getSubdirectory(name) != null) {
            current.removeSubdirectory(name);
            journal.log("Directory deleted: " + current.getPath() + name);
        } else {
            System.out.println("File or directory not found: " + name);
        }
    }

    private void createDirectory (String name) {
        if (name.isEmpty()) return;
        if (current.getSubdirectory(name) != null) {
            System.out.println("Directory already exists!");
            return;
        }
        Directory newDir = new Directory(name, current);
        current.addSubdirectory(newDir);
        journal.log("Directory created: " + current.getPath() + name);
    }

    private void changeDirectory(String name) {
        if (name.equals("..")) {
            if (current.getParent() != null) 
                current = current.getParent();
            else
                System.out.println("Directory not found.");
        }
    }
}