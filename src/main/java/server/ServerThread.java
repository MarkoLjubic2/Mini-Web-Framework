package server;

import framework.Discovery;
import framework.request.Header;
import framework.request.Helper;
import framework.request.Request;
import framework.request.enums.Method;
import framework.request.exceptions.RequestNotValidException;
import framework.response.JsonResponse;
import framework.response.Response;
import framework.di.DIEngine;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerThread implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private final Map<Class<?>, Object> controllerInstances = new HashMap<>();
    private static final Discovery discovery = new Discovery();
    private static final DIEngine diEngine = new DIEngine();

    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }

    public void run() {
        try {
            Request request = this.generateRequest();
            if (request == null) {
                in.close();
                out.close();
                socket.close();
                return;
            }

            Response response;
            if (request.getLocation().equals("/")) {
                // Response example
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("route_location", request.getLocation());
                responseMap.put("route_method", request.getMethod().toString());
                responseMap.put("parameters", request.getParameters());
                response = new JsonResponse(responseMap);
            } else {
                response = processTheRequest(request);
            }

            out.println(response.render());

            in.close();
            out.close();
            socket.close();

        } catch (IOException | RequestNotValidException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response processTheRequest(Request request) throws IOException, InvocationTargetException, IllegalAccessException, InstantiationException {
        String httpMethod = request.getMethod().toString();
        String path = request.getLocation();
        Discovery.Route route = discovery.getRoute(httpMethod, path);

        if (route == null) {
            return new JsonResponse(Map.of("error", "Route not found"));
        }

        Object controllerInstance = controllerInstances.computeIfAbsent(route.getClazz(), clazz -> {
            try {
                return diEngine.initialize(clazz);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        java.lang.reflect.Method method = route.getMethod();
        method.invoke(controllerInstance, request.getParameters());

        return new JsonResponse(Map.of("message", "Request processed"));
    }

    private Request generateRequest() throws IOException, RequestNotValidException {
        String command = in.readLine();
        if (command == null) {
            return null;
        }

        String[] actionRow = command.split(" ");
        Method method = Method.valueOf(actionRow[0]);
        String route = actionRow[1];
        Header header = new Header();
        HashMap<String, String> parameters = Helper.getParametersFromRoute(route);

        do {
            command = in.readLine();
            String[] headerRow = command.split(": ");
            if (headerRow.length == 2) {
                header.add(headerRow[0], headerRow[1]);
            }
        } while (!command.trim().isEmpty());

        if (method.equals(Method.POST)) {
            int contentLength = Integer.parseInt(header.get("content-length"));
            char[] buff = new char[contentLength];
            in.read(buff, 0, contentLength);
            String parametersString = new String(buff);

            HashMap<String, String> postParameters = Helper.getParametersFromString(parametersString);
            for (String parameterName : postParameters.keySet()) {
                parameters.put(parameterName, postParameters.get(parameterName));
            }
        }

        return new Request(method, route, header, parameters);
    }
}