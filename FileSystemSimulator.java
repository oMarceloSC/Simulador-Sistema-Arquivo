import java.util.Scanner;

public class FileSystemSimulator {
    private Directory root = new Directory("root");
    private Journal journal = new Journal();
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new FileSystemSimulator().start();
    }

    public void start() {
        System.out.println("Sistem File Simulator");

        while(true) {
            System.out.println("\n1. Create File\n2. List Files\n3. Del file\n4. leave");
            System.out.print("Choice: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> createFile();
                case "2" -> listFiles();
                case "3" -> deleteFile();
                case "4" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void createFile() {
        System.out.print("File name: ");
        String fileName = scanner.nextLine();
        System.out.print("File content: ");
        String fileContent = scanner.nextLine();

        File file = new File(fileName, fileContent);
        root.addFile(file);
        journal.log("File created: " + fileName);
    }

    private void listFiles() {
        if (root.getFiles().isEmpty()) {
            System.out.println("No files.");
            return;
        }
        System.out.println("Files:");
        for (String fileName : root.getFiles().keySet()) {
            System.out.println ("- " + fileName);
        }
    }

    private void deleteFile() {
        System.out.print("File name to delete: ");
        String name = scanner.nextLine();

        if (root.getFile(name) != null){
            root.removeFile(name);
            journal.log("File deleted: " + name);
            System.out.println("File deleted: " + name);
        } else {
            System.out.println("File not found: " + name);
        }
    }
}