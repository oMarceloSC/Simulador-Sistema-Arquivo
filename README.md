# Simulador de Sistema de Arquivos com Journaling

## Metodologia

O simulador foi desenvolvido em linguagem de programação Java. Ele recebe chamadas de métodos com os devidos parâmetros e implementa os métodos correspondentes aos comandos de um SO. O programa executa cada funcionalidade e exibe o resultado na tela quando necessário.

## Parte 1: Introdução ao Sistema de Arquivos com Journaling

### Sistema de Arquivos
Um sistema de arquivos é uma estrutura que organiza e controla como os dados são armazenados e recuperados em dispositivos de armazenamento. É essencial para o funcionamento de qualquer sistema operacional, fornecendo uma interface padronizada para manipulação de arquivos e diretórios.

### Journaling
O journaling é uma técnica que registra as operações realizadas no sistema de arquivos antes de executá-las efetivamente. Isso garante a integridade dos dados em caso de falhas do sistema, permitindo recuperação consistente. Os tipos principais incluem:
- **Write-ahead logging**: Registra operações antes da execução
- **Log-structured**: Organiza dados em formato de log sequencial

## Parte 2: Arquitetura do Simulador

### Estrutura de Dados
- **FileSystemItem**: Classe abstrata base para arquivos e diretórios
- **File**: Representa arquivos com conteúdo e metadados
- **Directory**: Representa diretórios com lista de itens filhos
- **FileSystemSimulator**: Classe principal que gerencia o sistema

### Journaling
- **JournalEntry**: Registra cada operação com timestamp
- **Journal**: Gerencia o log de operações e persistência
- Todas as operações são registradas antes da execução e commitadas após sucesso

## Parte 3: Implementação em Java

### Classes Principais
- **FileSystemSimulator**: Implementa todas as operações do sistema de arquivos
- **File e Directory**: Representam os elementos do sistema
- **Journal**: Gerencia o sistema de journaling com persistência em arquivo

### Operações Implementadas
- Copiar arquivos
- Apagar arquivos  
- Renomear arquivos
- Criar diretórios
- Apagar diretórios
- Renomear diretórios
- Listar conteúdo de diretórios

## Parte 4: Instalação e Funcionamento

### Recursos Necessários
- **Java JDK 8 ou superior**
- **IDE Java** (Eclipse, IntelliJ, VSCode) ou compilador javac
- **Sistema operacional**: Windows, Linux ou macOS

### Execução do Simulador

1. **Compilação**:
   ```bash
   javac FileSystemSimulator.java
   ```

2. **Execução**:
   ```bash
   java FileSystemSimulator
   ```

3. **Uso do Shell Interativo**:
   - O programa inicia em modo shell
   - Digite `help` para ver comandos disponíveis
   - Exemplo de comandos:
     - `createfile /teste.txt "Conteúdo do arquivo"`
     - `createdir /nova_pasta`
     - `list /`
     - `journal` (para ver log de operações)

### Funcionalidades
- **Modo Shell**: Interface interativa para execução de comandos
- **Journaling**: Todas as operações são registradas em arquivo
- **Persistência**: Journal salvo em `filesystem.journal`
- **Estrutura de exemplo**: Sistema inicia com diretórios e arquivos de demonstração

## Link do GitHub
https://github.com/oMarceloSC/Simulador-Sistema-Arquivo

## Resultados Esperados

O simulador fornece insights sobre o funcionamento de um sistema de arquivos real, demonstrando como as operações básicas são executadas e como o journaling garante a integridade dos dados. Com base nos resultados obtidos, é possível avaliar e entender como funciona esse elemento fundamental de um sistema operacional.