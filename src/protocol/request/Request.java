package protocol.request;

public class Request {
    private final Method method;
    private final String body;

    public Request(Method method, String body) {
        this.method = method;
        this.body = body;
    }

    public Method getMethod() {
        return method;
    }

    public String getBody() {
        return body;
    }
}
