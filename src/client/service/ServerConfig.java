package client.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class ServerConfig {

    //region Properties

    private static final String CONFIG_FILE = "server_info.dat";
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 503;

    private final String host;
    private final int port;

    //endregion

    //region Initialization

    public ServerConfig() {
        ServerInfo info = loadFile();
        this.host = info.host;
        this.port = info.port;
    }

    //endregion

    //region Configuration Loading

    private ServerInfo loadFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
            String host = reader.readLine();
            String portStr = reader.readLine();

            if (host == null || portStr == null) {
                throw new RuntimeException("Invalid server_info.dat format: missing host or port");
            }

            int port = Integer.parseInt(portStr.trim());
            System.out.println("Server config loaded from file: " + host + ":" + port);
            return new ServerInfo(host.trim(), port);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + CONFIG_FILE + ": " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid port number in " + CONFIG_FILE, e);
        }
    }

    //endregion

    //region Getters

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    //endregion

    //region Helper Classes

    private record ServerInfo(String host, int port) {}

    //endregion
}
