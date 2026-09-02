# Barbershop - JavaFX CRUD

Aplicação JavaFX para realizar operações CRUD (Create, Read, Update e Delete) sobre a entidade `Client` do sistema de barbearia.

## Visão geral

A interface gráfica foi projetada no arquivo FXML (Scene Builder style), com:
- cadastro de clientes;
- leitura/listagem de clientes em tabela;
- atualização dos dados de um cliente selecionado;
- exclusão de registros;
- persistência em SQLite via ORMLite;
- serialização em JSON e XML para exportação/importação de dados.

## Tecnologias

- Java 21+
- JavaFX
- Maven
- SQLite
- ORMLite
- JAXB
- Gson

## Requisitos

- JDK 21 ou superior
- Maven 3.9+
- Ambiente gráfico (Desktop) para executar a UI JavaFX

## Como executar

### Se `mvn` não for reconhecido no PowerShell

O Maven ainda não está no `PATH` do sistema. Em Windows, use uma destas opções:

Opção A — adicionar o Maven ao PATH da sessão atual:

   $env:Path += ";C:\Users\lucas\tools\apache-maven-3.9.16\bin"
   mvn -version
   mvn javafx:run

Opção B — chamar o executável diretamente:

   & "C:\Users\lucas\tools\apache-maven-3.9.16\bin\mvn.cmd" javafx:run

Opção C — adicionar permanentemente ao PATH no Windows:

1. Abra as Variáveis de Ambiente do sistema.
2. Edite `Path` e adicione:
   `C:\Users\lucas\tools\apache-maven-3.9.16\bin`
3. Feche e reabra o terminal.

A mensagem `JavaFX runtime components are missing` acontece quando o programa é executado sem informar o `module-path` correto do JavaFX. O caminho correto é usar o launcher do projeto ou o Maven/JavaFX em vez de `java -jar` direto.

Opção 1: via Maven (recomendado)

1. Abra um terminal na pasta do projeto.
2. Execute:

   mvn javafx:run

Opção 2: via arquivo de execução do Windows

1. Na pasta do projeto, execute:

   run-javafx.bat

Esse script compila e inicia a aplicação com os módulos JavaFX corretamente configurados.

Se quiser compilar manualmente:

   mvn clean package -DskipTests

E então iniciar com os módulos explicitamente:

   java --module-path "%USERPROFILE%\.m2\repository\org\openjfx\javafx-base\21.0.2\javafx-base-21.0.2-win.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-controls\21.0.2\javafx-controls-21.0.2-win.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-graphics\21.0.2\javafx-graphics-21.0.2-win.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-fxml\21.0.2\javafx-fxml-21.0.2-win.jar" --add-modules javafx.controls,javafx.fxml -jar target\Barbershop-1.0-SNAPSHOT.jar

A aplicação abrirá uma janela com o formulário de cadastro e a tabela de clientes.

## Estrutura principal

- `src/main/java/org/example/view/Barbershop.java` — inicialização da camada View JavaFX
- `src/main/java/org/example/view/barbershop.fxml` — layout visual em FXML, como no Scene Builder
- `src/main/java/org/example/controller/BarbershopController.java` — lógica e eventos da camada Controller
- `src/main/java/org/example/model/Client.java` — entidade da camada Model
- `src/main/java/org/example/model/ClientRepository.java` — persistência e serialização do Model
- `src/main/java/org/example/model/Database.java` — conexão SQLite do Model
- `src/main/java/org/example/model/ClientXmlSerializer.java` — serialização XML do Model

## Organização MVC

O projeto segue o padrão MVC: a camada **Model** concentra `Client`, `ClientRepository`, `Database` e `ClientXmlSerializer`; a camada **View** contém a classe JavaFX de inicialização e o arquivo FXML; e a camada **Controller** contém `BarbershopController`, responsável por receber os eventos da interface e coordenar as operações do Model.

## Observações

A aplicação já inicializa alguns clientes de exemplo quando o banco está vazio, permitindo validar o fluxo CRUD imediatamente após a primeira execução.

## Metodos responsaveis pelo CRUD

**CREATE — cadastrar cliente.** O metodo `salvarCliente()` do `BarbershopController` valida os campos, converte a idade e a data e chama `repository.create(cliente)` quando nenhum registro esta selecionado. O metodo `ClientRepository.create()` persiste a entidade `Client` no SQLite por meio do DAO do ORMLite. Depois, `carregarClientes()` atualiza a tabela.

**READ — consultar clientes.** O metodo `carregarClientes()` chama `repository.loadAll()` para consultar todos os clientes armazenados. O resultado e colocado na `ObservableList` `clientes`, que fica vinculada a `TableView` no metodo `configurarTabela()`. Assim, a interface apresenta os dados atuais do banco.

**UPDATE — atualizar cliente.** Ao selecionar uma linha da tabela, o listener de selecao guarda o objeto em `clienteSelecionado` e preenche o formulario. Ao salvar novamente, `salvarCliente()` altera os atributos da entidade e chama `repository.update(cliente)`. O DAO executa a atualizacao pelo ID, preservando o registro existente.

**DELETE — excluir cliente.** O metodo `excluirCliente()` exige um cliente selecionado e chama `repository.delete(cliente)`. O metodo `ClientRepository.delete()` remove o registro pelo DAO; em seguida, a selecao e limpa e `carregarClientes()` atualiza a tabela. A exclusao, portanto, e refletida no banco SQLite e na interface.
