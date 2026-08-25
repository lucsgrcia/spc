package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import jakarta.xml.bind.JAXBException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository {
    private static Database database;
    private static Dao<Client, Integer> dao;
    private List<Client> loadedClients;
    private Client loadedClient;
    private Gson gson;
    private ClientXmlSerializer xmlSerializer;

    public ClientRepository(Database database) throws JAXBException {
        ClientRepository.setDatabase(database);
        loadedClients = new ArrayList<Client>();
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .create();
        xmlSerializer = new ClientXmlSerializer();
    }

    public static void setDatabase(Database database) {
        ClientRepository.database = database;
        try {
            dao = DaoManager.createDao(database.getConnection(), Client.class);
            TableUtils.createTableIfNotExists(database.getConnection(), Client.class);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public Client create(Client client) {
        int nrows = 0;

        try {
            nrows = dao.create(client);

            if (nrows == 0)
                throw new SQLException("Error: object not saved");

            this.loadedClient = client;
            loadedClients.add(client);

        } catch (SQLException e) {
            System.out.println(e);
        }

        return client;
    }

    public void update(Client client) {
        try {
            int nrows = dao.update(client);

            if (nrows == 0)
                throw new SQLException("Error: object not updated");

            this.loadedClient = client;

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void delete(Client client) {
        try {
            int nrows = dao.delete(client);

            if (nrows == 0)
                throw new SQLException("Error: object not deleted");

            this.loadedClients.remove(client);

            if (this.loadedClient == client)
                this.loadedClient = null;

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public Client loadFromId(int id) {
        try {
            this.loadedClient = dao.queryForId(id);

        } catch (SQLException e) {
            System.out.println(e);
        }

        return this.loadedClient;
    }

    public List<Client> loadAll() {
        try {
            this.loadedClients = dao.queryForAll();

            if (!this.loadedClients.isEmpty())
                this.loadedClient = this.loadedClients.get(0);

        } catch (SQLException e) {
            System.out.println(e);
        }

        return this.loadedClients;
    }

    public String dumpData(String formato) {

        try {
            List<Client> clients = dao.queryForAll();

            if (formato.equalsIgnoreCase("JSON")) {

                return gson.toJson(clients);

            } else if (formato.equalsIgnoreCase("XML")) {

                return xmlSerializer.toXml(clients);

            } else {
                System.out.println("Formato não suportado.");
            }

        } catch (Exception e) {
            System.out.println("Erro ao serializar dados: " + e.getMessage());
        }

        return null;
    }

    public boolean dumpFile(String formato, File arquivo) {
        try {
            String data = dumpData(formato);

            FileWriter writer = new FileWriter(arquivo);
            writer.write(data);
            writer.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Client createFromJSON(String json) {

        try {
            Client client = gson.fromJson(json, Client.class);
            create(client);

            return client;
        } catch (Exception e) {
            System.out.println("Erro ao criar Client a partir do JSON: " + e.getMessage());

            return null;
        }
    }

    public Client createFromXML(String xml) {
        try {
            Client client = xmlSerializer.fromXml(xml);
            create(client);

            return client;
        } catch (JAXBException e) {
            System.out.println("Erro ao criar Client a partir do XML: " + e.getMessage());

            return null;
        }
    }

    public int importData(String formato, String data) {
        int count = 0;

        try {
            if (formato.equalsIgnoreCase("JSON")) {
                Client[] clients = gson.fromJson(data, Client[].class);

                for (Client client : clients) {
                    create(client);
                    count++;
                }

            } else if (formato.equalsIgnoreCase("XML")) {
                List<Client> clients = xmlSerializer.clientsFromXml(data);

                for (Client client : clients) {
                    create(client);
                    count++;
                }

            } else {
                System.out.println("Formato não suportado.");
            }

        } catch (Exception e) {
            System.out.println("Erro ao importar dados: " + e.getMessage());
        }

        return count;
    }

    public int importFile(String formato, File arquivo) {

        try {
            String data = java.nio.file.Files.readString(arquivo.toPath());

            return importData(formato, data);

        } catch (IOException e) {
            System.out.println("Erro ao importar arquivo: " + e.getMessage());

            return 0;
        }
    }

    public static Database getDatabase() {
        return database;
    }

    public static Dao<Client, Integer> getDao() {
        return dao;
    }

    public static void setDao(Dao<Client, Integer> dao) {
        ClientRepository.dao = dao;
    }

    public List<Client> getLoadedClients() {
        return loadedClients;
    }

    public void setLoadedClients(List<Client> loadedClients) {
        this.loadedClients = loadedClients;
    }

    public Client getLoadedClient() {
        return loadedClient;
    }

    public void setLoadedClient(Client loadedClient) {
        this.loadedClient = loadedClient;
    }
}