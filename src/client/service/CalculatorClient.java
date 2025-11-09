package client.service;

import protocol.request.*;
import protocol.response.*;
import shared.Operation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public final class CalculatorClient {

    //region Properties

    private final ServerConfig serverConfig;

    //endregion

    //region Initialization

    public CalculatorClient() {
        this.serverConfig = new ServerConfig();
    }

    public CalculatorClient(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    //endregion

    //region Request Processing

    public double calculate(Operation operation, double num1, double num2) throws IOException {
        String body = operation.name() + " " + num1 + " " + num2;
        Request request = new Request(Method.POST, body);

        Response response = sendRequest(request);

        if (response.getStatusCode() == StatusCode.OK) {
            return Double.parseDouble(response.getData());
        } else {
            throw new IOException("Server error: " + response.getMessage());
        }
    }

    private Response sendRequest(Request request) throws IOException {
        try (
            Socket socket = new Socket(serverConfig.getHost(), serverConfig.getPort());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            sendRequestMessage(out, request);
            return receiveResponse(in);
        }
    }

    private void sendRequestMessage(PrintWriter out, Request request) {
        out.println(request.getMethod().name());
        out.println(request.getBody());
    }

    private Response receiveResponse(BufferedReader in) throws IOException {
        String statusCodeLine = in.readLine();
        String messageLine = in.readLine();
        String dataLine = in.readLine();

        if (statusCodeLine == null || messageLine == null) {
            throw new IOException("Invalid response from server");
        }

        int code = Integer.parseInt(statusCodeLine);
        StatusCode statusCode = StatusCode.fromCode(code);
        String data = dataLine != null ? dataLine : "";

        return new Response(statusCode, messageLine, data);
    }

    //endregion

    //region Helper Methods

    public String getServerInfo() {
        return serverConfig.getHost() + ":" + serverConfig.getPort();
    }

    //endregion
}
