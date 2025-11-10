package server.controller;

import protocol.request.Request;
import protocol.response.Response;
import protocol.response.StatusCode;
import protocol.request.Method;
import server.exception.InvalidSyntaxException;
import server.exception.InvalidOperationException;
import server.service.exception.ServiceException;
import server.service.CalculatorService;
import core.NetworkLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public final class CalculatorController implements Runnable {

    //region Properties

    private final Socket clientSocket;
    private final CalculatorService calculatorService;

    //endregion

    //region Initialization

    public CalculatorController(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.calculatorService = new CalculatorService();
    }

    //endregion

    //region Request Processing

    @Override
    public void run() {
        String clientInfo = clientSocket.getInetAddress().toString();
        PrintWriter out = null;
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            out = writer;
            Request request = readRequest(in);
            NetworkLogger.logRequest("⬇️ RECEIVED", request, clientInfo);

            Response response = processRequest(request);
            NetworkLogger.logResponse("⬆️ SENDING", response, clientInfo);
            sendResponse(out, response);
        } catch (ServiceException e) {
            NetworkLogger.logError("Service", e);
            if (out != null) {
                Response errorResponse = new Response(StatusCode.BAD_REQUEST, e.getErrorType().message, "");
                NetworkLogger.logResponse("⬆️ ERROR", errorResponse, clientInfo);
                sendResponse(out, errorResponse);
            }
        } catch (IOException e) {
            NetworkLogger.logError("IO", e);
        } finally {
            closeSocket();
        }
    }

    private Request readRequest(BufferedReader in) throws IOException {
        String methodLine = in.readLine();
        String bodyLine = in.readLine();

        if (methodLine == null || bodyLine == null) {
            throw new InvalidSyntaxException();
        }

        Method method;
        try {
            method = Method.fromString(methodLine);
        } catch (IllegalArgumentException e) {
            throw new InvalidOperationException();
        }

        return new Request(method, bodyLine);
    }

    private Response processRequest(Request request) {
        try {
            String body = request.getBody();
            double result = calculatorService.calculate(body);
            return new Response(StatusCode.OK, "Success", String.valueOf(result));

        } catch (ServiceException e) {
            return new Response(StatusCode.BAD_REQUEST, e.getErrorType().message, "");
        } catch (Exception e) {
            return new Response(StatusCode.INTERNAL_SERVER_ERROR, "Internal server error", "");
        }
    }

    private void sendResponse(PrintWriter out, Response response) {
        out.println(response.getStatusCode().code);
        out.println(response.getMessage());
        out.println(response.getData());
        System.out.println("Response sent: " + response.getStatusCode().code);
    }

    private void closeSocket() {
        try {
            clientSocket.close();
            System.out.println("Client disconnected: " + clientSocket.getInetAddress());
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }

    //endregion
}
