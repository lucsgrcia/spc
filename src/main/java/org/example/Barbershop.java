package org.example;

import jakarta.xml.bind.JAXBException;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class Barbershop {

    public static void main(String[] args) throws ParseException, JAXBException {

        // =====================================================
        // CONFIGURAÇÃO
        // =====================================================

        Database db = new Database("barbershop.db");
        ClientRepository repository = new ClientRepository(db);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // =====================================================
        // CREATE
        // =====================================================

        List<Client> clients = repository.loadAll();

        // Caso o banco esteja vazio, cria os clientes iniciais
        if (clients.isEmpty()) {

            Client c1 = new Client("Lucas", "11111", 23, formatter.parse("20/08/2026 14:30"));
            Client c2 = new Client("Mateus", "22222", 20, formatter.parse("19/08/2026 10:00"));
            Client c3 = new Client("Marcos", "33333", 30, formatter.parse("19/08/2026 20:00"));

            repository.create(c1);
            repository.create(c2);
            repository.create(c3);

            System.out.println("Clientes criados!");
            System.out.println(c1);
            System.out.println(c2);
            System.out.println(c3);
        }

        // =====================================================
        // READ ALL
        // =====================================================

        System.out.println("\n==================================");
        System.out.println("READ ALL");
        System.out.println("==================================");

        repository.loadAll().forEach(System.out::println);

        // =====================================================
        // READ BY ID
        // =====================================================

        clients = repository.loadAll();

        if (!clients.isEmpty()) {
            Client client = clients.get(0);

            System.out.println("\nExibir o primeiro cliente pelo ID:");

            Client foundClient = repository.loadFromId(client.getId());

            if (foundClient != null) {
                System.out.println("\nCliente encontrado:");
                System.out.println("ID: " + foundClient.getId());
                System.out.println("Nome: " + foundClient.getName());
                System.out.println("CPF: " + foundClient.getCpf());
            }
        }

        // =====================================================
        // UPDATE
        // =====================================================

        clients = repository.loadAll();

        if (!clients.isEmpty()) {
            Client client = clients.get(0);

            System.out.println("\nAtualizar nome do cliente:");

            client.setName("Lucas Garcia");
            repository.update(client);

            System.out.println("\nCliente atualizado!");
            System.out.println("Novo nome: " + client.getName());
            System.out.println(client);
        }

        // =====================================================
        // DELETE
        // =====================================================

        clients = repository.loadAll();

        if (clients.size() >= 2) {
            Client client = clients.get(1);

            System.out.println("\nRemovendo o cliente: " + client);

            repository.delete(client);

            System.out.println("Cliente deletado!");
        }

        // =====================================================
        // DUMP DATA - JSON
        // =====================================================

        System.out.println("\n==================================");
        System.out.println("DUMP DATA - JSON");
        System.out.println("==================================");

        String clientsJSON = repository.dumpData("JSON");
        System.out.println(clientsJSON);

        // =====================================================
        // DUMP DATA - XML
        // =====================================================

        System.out.println("\n==================================");
        System.out.println("DUMP DATA - XML");
        System.out.println("==================================");

        String clientsXML = repository.dumpData("XML");
        System.out.println(clientsXML);

        // =====================================================
        // DUMP FILE - JSON
        // =====================================================

        System.out.println("\n==================================");
        System.out.println("DUMP FILE - JSON");
        System.out.println("==================================");

        File jsonFile = new File("clients.json");
        boolean jsonSaved = repository.dumpFile("JSON", jsonFile);

        System.out.println("Arquivo JSON salvo: " + jsonSaved);
        System.out.println("Caminho: " + jsonFile.getAbsolutePath());

        // =====================================================
        // DUMP FILE - XML
        // =====================================================

        System.out.println("\n==================================");
        System.out.println("DUMP FILE - XML");
        System.out.println("==================================");

        File xmlFile = new File("clients.xml");
        boolean xmlSaved = repository.dumpFile("XML", xmlFile);

        System.out.println("Arquivo XML salvo: " + xmlSaved);
        System.out.println("Caminho: " + xmlFile.getAbsolutePath());

        // =====================================================
        // CREATE FROM JSON
        // =====================================================

        System.out.println("\n==================================");
        System.out.println("CREATE FROM JSON");
        System.out.println("==================================");

        String newClientJSON = """
                {
                  "name": "João",
                  "cpf": "44444",
                  "age": 25,
                  "date": "2026-08-21 15:00:00"
                }
                """;

        Client clientFromJSON = repository.createFromJSON(newClientJSON);

        System.out.println("Cliente criado a partir do JSON:");
        System.out.println(clientFromJSON);

        // =====================================================
        // CREATE FROM XML
        // =====================================================

        System.out.println("\n==================================");
        System.out.println("CREATE FROM XML");
        System.out.println("==================================");

        String newClientXML = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <client>
                    <id>0</id>
                    <name>Pedro</name>
                    <cpf>55555</cpf>
                    <age>28</age>
                    <date>2026-08-22T10:00:00</date>
                </client>
                """;

        Client clientFromXML = repository.createFromXML(newClientXML);

        System.out.println("Cliente criado a partir do XML:");
        System.out.println(clientFromXML);

        // =====================================================
        // IMPORT DATA - JSON
        // =====================================================

        System.out.println("\n==================================");
        System.out.println("IMPORT DATA - JSON");
        System.out.println("==================================");

        String importJSON = """
                [
                  {
                    "name": "Carlos",
                    "cpf": "66666",
                    "age": 35,
                    "date": "2026-08-23 09:00:00"
                  },
                  {
                    "name": "Ana",
                    "cpf": "77777",
                    "age": 27,
                    "date": "2026-08-23 11:00:00"
                  }
                ]
                """;

        int importedJSON = repository.importData("JSON", importJSON);

        System.out.println("Quantidade de clientes importados: " + importedJSON);

        // =====================================================
        // IMPORT DATA - XML
        // =====================================================

        System.out.println("\n==================================");
        System.out.println("IMPORT DATA - XML");
        System.out.println("==================================");

        String importXML = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <clients>
                    <client>
                        <id>0</id>
                        <name>Fernanda</name>
                        <cpf>88888</cpf>
                        <age>31</age>
                        <date>2026-08-24T14:00:00</date>
                    </client>
                    <client>
                        <id>0</id>
                        <name>Rafael</name>
                        <cpf>99999</cpf>
                        <age>29</age>
                        <date>2026-08-24T16:00:00</date>
                    </client>
                </clients>
                """;

        int importedXML = repository.importData("XML", importXML);

        System.out.println("Quantidade de clientes importados: " + importedXML);

        // =====================================================
        // IMPORT FILE - JSON
        // =====================================================

        System.out.println("\n==================================");
        System.out.println("IMPORT FILE - JSON");
        System.out.println("==================================");

        int importedJSONFile = repository.importFile("JSON", jsonFile);

        System.out.println("Quantidade importada do arquivo JSON: " + importedJSONFile);

        // =====================================================
        // IMPORT FILE - XML
        // =====================================================

        System.out.println("\n==================================");
        System.out.println("IMPORT FILE - XML");
        System.out.println("==================================");

        int importedXMLFile = repository.importFile("XML", xmlFile);

        System.out.println("Quantidade importada do arquivo XML: " + importedXMLFile);

        // =====================================================
        // EXIBIR ESTADO FINAL DO BANCO
        // =====================================================

        System.out.println("\n==================================");
        System.out.println("CLIENTES NO BANCO");
        System.out.println("==================================");

        repository.loadAll().forEach(System.out::println);

        // =====================================================
        // FECHAR BANCO
        // =====================================================

        db.close();
    }
}