import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

// Classe que representa um item no sistema de arquivos (arquivo ou diretório)
abstract class FileSystemItem {
    protected String name;
    protected String path;
    protected Date creationDate;
    protected Date lastModified;
    
    public FileSystemItem(String name, String path) {
        this.name = name;
        this.path = path;
        this.creationDate = new Date();
        this.lastModified = new Date();
    }
    
    public String getName() { return name; }
    public String getPath() { return path; }
    public Date getCreationDate() { return creationDate; }
    public Date getLastModified() { return lastModified; }
    
    public void setName(String name) { 
        this.name = name; 
        this.lastModified = new Date();
    }
    
    public void setPath(String path) { 
        this.path = path; 
        this.lastModified = new Date();
    }
    
    public abstract boolean isDirectory();
    public abstract String getType();
}

// Classe que representa um arquivo
class File extends FileSystemItem {
    private String content;
    private long size;
    
    public File(String name, String path) {
        super(name, path);
        this.content = "";
        this.size = 0;
    }
    
    public File(String name, String path, String content) {
        super(name, path);
        this.content = content;
        this.size = content.length();
    }
    
    @Override
    public boolean isDirectory() { return false; }
    
    @Override
    public String getType() { return "FILE"; }
    
    public String getContent() { return content; }
    public long getSize() { return size; }
    
    public void setContent(String content) {
        this.content = content;
        this.size = content.length();
        this.lastModified = new Date();
    }
}

// Classe que representa um diretório
class Directory extends FileSystemItem {
    private List<FileSystemItem> children;
    
    public Directory(String name, String path) {
        super(name, path);
        this.children = new ArrayList<>();
    }
    
    @Override
    public boolean isDirectory() { return true; }
    
    @Override
    public String getType() { return "DIRECTORY"; }
    
    public List<FileSystemItem> getChildren() { return children; }
    
    public void addChild(FileSystemItem item) {
        children.add(item);
        this.lastModified = new Date();
    }
    
    public boolean removeChild(String name) {
        boolean removed = children.removeIf(item -> item.getName().equals(name));
        if (removed) {
            this.lastModified = new Date();
        }
        return removed;
    }
    
    public FileSystemItem findChild(String name) {
        return children.stream()
                .filter(item -> item.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}

// Classe para registrar operações no journal
class JournalEntry {
    private String operation;
    private String details;
    private Date timestamp;
    private boolean committed;
    
    public JournalEntry(String operation, String details) {
        this.operation = operation;
        this.details = details;
        this.timestamp = new Date();
        this.committed = false;
    }
    
    public String getOperation() { return operation; }
    public String getDetails() { return details; }
    public Date getTimestamp() { return timestamp; }
    public boolean isCommitted() { return committed; }
    
    public void commit() { this.committed = true; }
    
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s: %s (Status: %s)", 
                sdf.format(timestamp), operation, details, 
                committed ? "COMMITTED" : "PENDING");
    }
}

// Classe responsável pelo sistema de journaling
class Journal {
    private List<JournalEntry> entries;
    private String journalFile;
    
    public Journal(String journalFile) {
        this.journalFile = journalFile;
        this.entries = new ArrayList<>();
        loadJournal();
    }
    
    public void logOperation(String operation, String details) {
        JournalEntry entry = new JournalEntry(operation, details);
        entries.add(entry);
        saveJournal();
        System.out.println("Journal: " + entry);
    }
    
    public void commitOperation() {
        if (!entries.isEmpty()) {
            JournalEntry lastEntry = entries.get(entries.size() - 1);
            lastEntry.commit();
            saveJournal();
            System.out.println("Journal: Operação commitada - " + lastEntry.getOperation());
        }
    }
    
    public void showJournal() {
        System.out.println("\n=== JOURNAL LOG ===");
        if (entries.isEmpty()) {
            System.out.println("Nenhuma operação registrada.");
        } else {
            for (JournalEntry entry : entries) {
                System.out.println(entry);
            }
        }
        System.out.println("==================\n");
    }
    
    private void saveJournal() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(journalFile))) {
            for (JournalEntry entry : entries) {
                writer.println(entry.toString());
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar journal: " + e.getMessage());
        }
    }
    
    private void loadJournal() {
        java.io.File file = new java.io.File(journalFile);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(journalFile))) {
                String line;
                System.out.println("Journal carregado de: " + journalFile);
            } catch (IOException e) {
                System.err.println("Erro ao carregar journal: " + e.getMessage());
            }
        }
    }
}

// Classe principal do simulador
public class FileSystemSimulator {
    private Directory root;
    private Directory currentDirectory;
    private Journal journal;
    private Scanner scanner;
    
    public FileSystemSimulator() {
        this.root = new Directory("root", "/");
        this.currentDirectory = root;
        this.journal = new Journal("filesystem.journal");
        this.scanner = new Scanner(System.in);
        
        journal.logOperation("INIT", "Sistema de arquivos inicializado");
        journal.commitOperation();
    }
    
    // Método para copiar arquivo
    public boolean copyFile(String sourcePath, String destPath) {
        try {
            journal.logOperation("COPY_FILE", "Origem: " + sourcePath + " -> Destino: " + destPath);
            
            FileSystemItem source = findItem(sourcePath);
            if (source == null || source.isDirectory()) {
                System.out.println("Erro: Arquivo origem não encontrado ou é um diretório");
                return false;
            }
            
            String destDir = getParentPath(destPath);
            String destName = getFileName(destPath);
            
            Directory destDirectory = (Directory) findItem(destDir);
            if (destDirectory == null || !destDirectory.isDirectory()) {
                System.out.println("Erro: Diretório de destino não encontrado");
                return false;
            }
            
            File sourceFile = (File) source;
            File newFile = new File(destName, destPath, sourceFile.getContent());
            destDirectory.addChild(newFile);
            
            journal.commitOperation();
            System.out.println("Arquivo copiado com sucesso: " + sourcePath + " -> " + destPath);
            return true;
            
        } catch (Exception e) {
            System.out.println("Erro ao copiar arquivo: " + e.getMessage());
            return false;
        }
    }
    
    // Método para apagar arquivo
    public boolean deleteFile(String filePath) {
        try {
            journal.logOperation("DELETE_FILE", "Arquivo: " + filePath);
            
            String parentPath = getParentPath(filePath);
            String fileName = getFileName(filePath);
            
            Directory parentDir = (Directory) findItem(parentPath);
            if (parentDir == null) {
                System.out.println("Erro: Diretório pai não encontrado");
                return false;
            }
            
            FileSystemItem item = parentDir.findChild(fileName);
            if (item == null || item.isDirectory()) {
                System.out.println("Erro: Arquivo não encontrado ou é um diretório");
                return false;
            }
            
            parentDir.removeChild(fileName);
            journal.commitOperation();
            System.out.println("Arquivo apagado com sucesso: " + filePath);
            return true;
            
        } catch (Exception e) {
            System.out.println("Erro ao apagar arquivo: " + e.getMessage());
            return false;
        }
    }
    
    // Método para renomear arquivo
    public boolean renameFile(String oldPath, String newName) {
        try {
            journal.logOperation("RENAME_FILE", "De: " + oldPath + " Para: " + newName);
            
            FileSystemItem item = findItem(oldPath);
            if (item == null || item.isDirectory()) {
                System.out.println("Erro: Arquivo não encontrado ou é um diretório");
                return false;
            }
            
            String newPath = getParentPath(oldPath) + "/" + newName;
            item.setName(newName);
            item.setPath(newPath);
            
            journal.commitOperation();
            System.out.println("Arquivo renomeado com sucesso: " + oldPath + " -> " + newName);
            return true;
            
        } catch (Exception e) {
            System.out.println("Erro ao renomear arquivo: " + e.getMessage());
            return false;
        }
    }
    
    // Método para criar diretório
    public boolean createDirectory(String dirPath) {
        try {
            journal.logOperation("CREATE_DIR", "Diretório: " + dirPath);
            
            String parentPath = getParentPath(dirPath);
            String dirName = getFileName(dirPath);
            
            Directory parentDir = (Directory) findItem(parentPath);
            if (parentDir == null) {
                System.out.println("Erro: Diretório pai não encontrado");
                return false;
            }
            
            if (parentDir.findChild(dirName) != null) {
                System.out.println("Erro: Item com esse nome já existe");
                return false;
            }
            
            Directory newDir = new Directory(dirName, dirPath);
            parentDir.addChild(newDir);
            
            journal.commitOperation();
            System.out.println("Diretório criado com sucesso: " + dirPath);
            return true;
            
        } catch (Exception e) {
            System.out.println("Erro ao criar diretório: " + e.getMessage());
            return false;
        }
    }
    
    // Método para apagar diretório
    public boolean deleteDirectory(String dirPath) {
        try {
            journal.logOperation("DELETE_DIR", "Diretório: " + dirPath);
            
            String parentPath = getParentPath(dirPath);
            String dirName = getFileName(dirPath);
            
            Directory parentDir = (Directory) findItem(parentPath);
            if (parentDir == null) {
                System.out.println("Erro: Diretório pai não encontrado");
                return false;
            }
            
            FileSystemItem item = parentDir.findChild(dirName);
            if (item == null || !item.isDirectory()) {
                System.out.println("Erro: Diretório não encontrado");
                return false;
            }
            
            Directory dirToDelete = (Directory) item;
            if (!dirToDelete.getChildren().isEmpty()) {
                System.out.println("Erro: Diretório não está vazio");
                return false;
            }
            
            parentDir.removeChild(dirName);
            journal.commitOperation();
            System.out.println("Diretório apagado com sucesso: " + dirPath);
            return true;
            
        } catch (Exception e) {
            System.out.println("Erro ao apagar diretório: " + e.getMessage());
            return false;
        }
    }
    
    // Método para renomear diretório
    public boolean renameDirectory(String oldPath, String newName) {
        try {
            journal.logOperation("RENAME_DIR", "De: " + oldPath + " Para: " + newName);
            
            FileSystemItem item = findItem(oldPath);
            if (item == null || !item.isDirectory()) {
                System.out.println("Erro: Diretório não encontrado");
                return false;
            }
            
            String newPath = getParentPath(oldPath) + "/" + newName;
            item.setName(newName);
            item.setPath(newPath);
            
            journal.commitOperation();
            System.out.println("Diretório renomeado com sucesso: " + oldPath + " -> " + newName);
            return true;
            
        } catch (Exception e) {
            System.out.println("Erro ao renomear diretório: " + e.getMessage());
            return false;
        }
    }
    
    // Método para listar conteúdo de um diretório
    public void listDirectory(String dirPath) {
        try {
            journal.logOperation("LIST_DIR", "Diretório: " + dirPath);
            
            Directory dir = (Directory) findItem(dirPath);
            if (dir == null || !dir.isDirectory()) {
                System.out.println("Erro: Diretório não encontrado");
                return;
            }
            
            System.out.println("\n=== Conteúdo de " + dirPath + " ===");
            if (dir.getChildren().isEmpty()) {
                System.out.println("Diretório vazio");
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                for (FileSystemItem item : dir.getChildren()) {
                    String type = item.isDirectory() ? "[DIR]" : "[FILE]";
                    String size = item.isDirectory() ? "" : " (" + ((File)item).getSize() + " bytes)";
                    System.out.printf("%s %-20s %s%s\n", 
                            type, item.getName(), 
                            sdf.format(item.getLastModified()), size);
                }
            }
            System.out.println("================================\n");
            
            journal.commitOperation();
            
        } catch (Exception e) {
            System.out.println("Erro ao listar diretório: " + e.getMessage());
        }
    }
    
    // Método para criar arquivo com conteúdo
    public boolean createFile(String filePath, String content) {
        try {
            journal.logOperation("CREATE_FILE", "Arquivo: " + filePath);
            
            String parentPath = getParentPath(filePath);
            String fileName = getFileName(filePath);
            
            Directory parentDir = (Directory) findItem(parentPath);
            if (parentDir == null) {
                System.out.println("Erro: Diretório pai não encontrado");
                return false;
            }
            
            if (parentDir.findChild(fileName) != null) {
                System.out.println("Erro: Item com esse nome já existe");
                return false;
            }
            
            File newFile = new File(fileName, filePath, content);
            parentDir.addChild(newFile);
            
            journal.commitOperation();
            System.out.println("Arquivo criado com sucesso: " + filePath);
            return true;
            
        } catch (Exception e) {
            System.out.println("Erro ao criar arquivo: " + e.getMessage());
            return false;
        }
    }
    
    // Métodos auxiliares
    private FileSystemItem findItem(String path) {
        if (path.equals("/")) return root;
        
        String[] parts = path.split("/");
        FileSystemItem current = root;
        
        for (String part : parts) {
            if (part.isEmpty()) continue;
            
            if (!current.isDirectory()) return null;
            
            Directory currentDir = (Directory) current;
            current = currentDir.findChild(part);
            
            if (current == null) return null;
        }
        
        return current;
    }
    
    private String getParentPath(String path) {
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash <= 0) return "/";
        return path.substring(0, lastSlash);
    }
    
    private String getFileName(String path) {
        int lastSlash = path.lastIndexOf('/');
        return path.substring(lastSlash + 1);
    }
    
    // Modo Shell - Interface interativa
    public void runShell() {
        System.out.println("=== SIMULADOR DE SISTEMA DE ARQUIVOS ===");
        System.out.println("Digite 'help' para ver os comandos disponíveis");
        System.out.println("Digite 'exit' para sair\n");
        
        while (true) {
            System.out.print("fs> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) continue;
            
            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();
            
            switch (command) {
                case "help":
                    showHelp();
                    break;
                    
                case "exit":
                    System.out.println("Saindo do simulador...");
                    return;
                    
                case "createfile":
                    if (parts.length >= 2) {
                        String content = parts.length > 2 ? String.join(" ", Arrays.copyOfRange(parts, 2, parts.length)) : "";
                        createFile(parts[1], content);
                    } else {
                        System.out.println("Uso: createfile <caminho> [conteúdo]");
                    }
                    break;
                    
                case "copyfile":
                    if (parts.length == 3) {
                        copyFile(parts[1], parts[2]);
                    } else {
                        System.out.println("Uso: copyfile <origem> <destino>");
                    }
                    break;
                    
                case "deletefile":
                    if (parts.length == 2) {
                        deleteFile(parts[1]);
                    } else {
                        System.out.println("Uso: deletefile <caminho>");
                    }
                    break;
                    
                case "renamefile":
                    if (parts.length == 3) {
                        renameFile(parts[1], parts[2]);
                    } else {
                        System.out.println("Uso: renamefile <caminho_atual> <novo_nome>");
                    }
                    break;
                    
                case "createdir":
                    if (parts.length == 2) {
                        createDirectory(parts[1]);
                    } else {
                        System.out.println("Uso: createdir <caminho>");
                    }
                    break;
                    
                case "deletedir":
                    if (parts.length == 2) {
                        deleteDirectory(parts[1]);
                    } else {
                        System.out.println("Uso: deletedir <caminho>");
                    }
                    break;
                    
                case "renamedir":
                    if (parts.length == 3) {
                        renameDirectory(parts[1], parts[2]);
                    } else {
                        System.out.println("Uso: renamedir <caminho_atual> <novo_nome>");
                    }
                    break;
                    
                case "list":
                    String path = parts.length > 1 ? parts[1] : "/";
                    listDirectory(path);
                    break;
                    
                case "journal":
                    journal.showJournal();
                    break;
                    
                default:
                    System.out.println("Comando não reconhecido. Digite 'help' para ajuda.");
            }
        }
    }
    
    private void showHelp() {
        System.out.println("\n=== COMANDOS DISPONÍVEIS ===");
        System.out.println("createfile <caminho> [conteúdo] - Criar arquivo");
        System.out.println("copyfile <origem> <destino>     - Copiar arquivo");
        System.out.println("deletefile <caminho>            - Apagar arquivo");
        System.out.println("renamefile <atual> <novo>       - Renomear arquivo");
        System.out.println("createdir <caminho>             - Criar diretório");
        System.out.println("deletedir <caminho>             - Apagar diretório");
        System.out.println("renamedir <atual> <novo>        - Renomear diretório");
        System.out.println("list [caminho]                  - Listar conteúdo (padrão: /)");
        System.out.println("journal                         - Mostrar log de operações");
        System.out.println("help                            - Mostrar esta ajuda");
        System.out.println("exit                            - Sair do simulador");
        System.out.println("============================\n");
    }
    
    // Método main para execução
    public static void main(String[] args) {
        FileSystemSimulator simulator = new FileSystemSimulator();
        
        // Criar alguns arquivos e diretórios de exemplo
        simulator.createDirectory("/docs");
        simulator.createDirectory("/temp");
        simulator.createFile("/docs/readme.txt", "Este é um arquivo de exemplo");
        simulator.createFile("/temp/log.txt", "Log de operações do sistema");
        
        System.out.println("Sistema inicializado com estrutura de exemplo:");
        simulator.listDirectory("/");
        
        // Executar modo shell
        simulator.runShell();
    }
}