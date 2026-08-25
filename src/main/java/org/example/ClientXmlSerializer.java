package org.example;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class ClientXmlSerializer {

    private JAXBContext context;

    public ClientXmlSerializer() throws JAXBException {
        context = JAXBContext.newInstance(Client.class, ClientsList.class);
    }

    // Client -> XML
    public String toXml(Client client) throws JAXBException {
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        marshaller.marshal(client, sw);

        return sw.toString();
    }

    // List<Client> -> XML
    public String toXml(List<Client> clients) throws JAXBException {
        ClientsList clientsList = new ClientsList(clients);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        marshaller.marshal(clientsList, sw);

        return sw.toString();
    }

    // XML -> Client
    public Client fromXml(String xml) throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader sr = new StringReader(xml);

        return (Client) unmarshaller.unmarshal(sr);
    }

    // XML -> List<Client>
    public List<Client> clientsFromXml(String xml) throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader sr = new StringReader(xml);
        ClientsList clientsList = (ClientsList) unmarshaller.unmarshal(sr);

        return clientsList.getClients();
    }


    // Classe auxiliar para representar vários Client no XML
    @XmlRootElement(name = "clients")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ClientsList {

        @XmlElement(name = "client")
        private List<Client> clients;

        public ClientsList() {
        }

        public ClientsList(List<Client> clients) {
            this.clients = clients;
        }

        public List<Client> getClients() {
            return clients;
        }

        public void setClients(List<Client> clients) {
            this.clients = clients;
        }
    }
}