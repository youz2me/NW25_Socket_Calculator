package core;

import protocol.request.Request;
import protocol.response.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class NetworkLogger {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    //region Request Logging

    public static void logRequest(String direction, Request request, String clientInfo) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        StringBuilder log = new StringBuilder();
        log.append("----------------------------------------------------\n");
        log.append("1️⃣ ").append(direction).append(" REQUEST [").append(timestamp).append("]\n");
        log.append("----------------------------------------------------\n");
        log.append("2️⃣ Client: ").append(clientInfo).append("\n");
        log.append("   Method: ").append(request.getMethod().name()).append("\n");
        log.append("   Body: ").append(request.getBody()).append("\n");
        log.append("------------------- END REQUEST -------------------");
        System.out.println(log);
    }

    //endregion

    //region Response Logging

    public static void logResponse(String direction, Response response, String clientInfo) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        StringBuilder log = new StringBuilder();
        log.append("------------------- RESPONSE -------------------\n");
        log.append("3️⃣ ").append(direction).append(" RESPONSE [").append(timestamp).append("]\n");
        log.append("   Client: ").append(clientInfo).append("\n");
        log.append("   Status Code: [").append(response.getStatusCode().code).append("]\n");
        log.append("   Message: ").append(response.getMessage()).append("\n");
        log.append("4️⃣ Data: ").append(response.getData()).append("\n");
        log.append("------------------- END RESPONSE -------------------");
        System.out.println(log);
    }

    //endregion

    //region Error Logging

    public static void logError(String context, Exception error) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        StringBuilder log = new StringBuilder();
        log.append("❌ ERROR in ").append(context).append(" [").append(timestamp).append("]\n");
        log.append("   Type: ").append(error.getClass().getSimpleName()).append("\n");
        log.append("   Message: ").append(error.getMessage()).append("\n");
        log.append("------------------- END ERROR -------------------");
        System.err.println(log);
    }

    //endregion

    //region Connection Logging

    public static void logConnection(String event, String info) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        System.out.println("🔌 " + event + ": " + info + " [" + timestamp + "]");
    }

    //endregion
}
