package client.service;

import client.exception.ConnectionFailedException;
import client.exception.InvalidResponseException;
import client.exception.ServerErrorException;
import protocol.request.*;
import protocol.response.*;
import shared.NetworkLogger;
import shared.Operation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public final class NetworkService {

    //region Properties

    private final NetworkConfiguration networkConfiguration;

    //endregion

    //region Initialization

    public NetworkService() {
        this.networkConfiguration = new NetworkConfiguration();
    }

    public NetworkService(NetworkConfiguration networkConfiguration) {
        this.networkConfiguration = networkConfiguration;
    }

    //endregion

    //region Request Processing

    public double calculate(Operation operation, double num1, double num2) {
        String body = operation.name() + " " + num1 + " " + num2;
        Request request = new Request(Method.POST, body);

        Response response = sendRequest(request);

        if (response.getStatusCode() == StatusCode.OK) {
            return Double.parseDouble(response.getData());
        } else {
            throw new ServerErrorException(response.getStatusCode(), response.getMessage());
        }
    }

    private Response sendRequest(Request request) {
        String serverInfo = networkConfiguration.getHost() + ":" + networkConfiguration.getPort();

        try (
            Socket socket = new Socket(networkConfiguration.getHost(), networkConfiguration.getPort());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            NetworkLogger.logRequest("⬆️ SENDING", request, serverInfo);
            sendRequestMessage(out, request);

            Response response = receiveResponse(in);
            NetworkLogger.logResponse("⬇️ RECEIVED", response, serverInfo);
            return response;
        } catch (IOException e) {
            NetworkLogger.logError("Connection", e);
            throw new ConnectionFailedException();
        }
    }

    private void sendRequestMessage(PrintWriter out, Request request) {
        out.println(request.getMethod().name());
        out.println(request.getBody());
    }

    private Response receiveResponse(BufferedReader in) throws IOException {
        try {
            String statusCodeLine = in.readLine();
            String messageLine = in.readLine();
            String dataLine = in.readLine();

            if (statusCodeLine == null || messageLine == null) {
                throw new InvalidResponseException();
            }

            int code = Integer.parseInt(statusCodeLine);
            StatusCode statusCode = StatusCode.fromCode(code);
            String data = dataLine != null ? dataLine : "";

            return new Response(statusCode, messageLine, data);
        } catch (NumberFormatException e) {
            throw new InvalidResponseException();
        }
    }

    //endregion

    //region Helper Methods

    public String getServerInfo() {
        return networkConfiguration.getHost() + ":" + networkConfiguration.getPort();
    }

    //endregion
}
