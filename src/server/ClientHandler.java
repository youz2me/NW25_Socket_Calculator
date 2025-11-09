package server;

import protocol.request.Method;
import protocol.request.Request;
import protocol.response.Response;
import protocol.response.StatusCode;
import protocol.response.ErrorType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    //region Properties

    private final Socket clientSocket;
    private final Calculator calculator;

    //endregion

    //region Initialization

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.calculator = new Calculator();
    }

    //endregion

    //region Request Processing

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String requestMessage = readRequest(in);
            Response response = processRequest(requestMessage);
            sendResponse(out, response);
        } catch (IOException e) {
            System.err.println("Client handler error: " + e.getMessage());
        } finally {
            closeSocket();
        }
    }

    private String readRequest(BufferedReader in) throws IOException {
        StringBuilder message = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null && !line.isEmpty()) {
            message.append(line).append("\n");
        }

        // 마지막 줄 추가 (빈 줄이 아닌 경우)
        if ((line = in.readLine()) != null) {
            message.append(line);
        }

        return message.toString().trim();
    }

    private Response processRequest(String requestMessage) {
        try {
            Request request = new Request(Method.GET, requestMessage);
            String body = request.getBody();

            double result = calculator.calculate(body);
            return new Response(StatusCode.OK, String.valueOf(result));

        } catch (IllegalArgumentException e) {
            return new Response(StatusCode.BAD_REQUEST, ErrorType.INVALID_OPERATION.message);
        } catch (ArithmeticException e) {
            return new Response(StatusCode.BAD_REQUEST, ErrorType.DIVIDE_BY_ZERO.message);
        } catch (Exception e) {
            return new Response(StatusCode.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private void sendResponse(PrintWriter out, Response response) {
        out.println(response.getBody());
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
