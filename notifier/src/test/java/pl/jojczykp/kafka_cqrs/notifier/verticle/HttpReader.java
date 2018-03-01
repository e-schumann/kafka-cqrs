package pl.jojczykp.kafka_cqrs.notifier.verticle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class HttpReader {

    private volatile HttpURLConnection connection;
    private volatile BufferedReader reader;

    private HttpReader() {}

    public static HttpReader connect(String urlString) {
        HttpReader client = new HttpReader();
        client.open(urlString);

        return client;
    }

    private synchronized void open(String urlString) {
        try {
            connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (Exception e) {
            close();
            throw new RuntimeException(e);
        }
    }

    public synchronized int getResponseCode() {
        try {
            return connection.getResponseCode();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized Map<String, List<String>> getHeaderFields() {
        return connection.getHeaderFields();
    }

    public synchronized String readLine() {
        try {
            return reader.ready() ? reader.readLine() : null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void close() {
        closeReader();
        closeConnection();
    }

    private void closeReader() {
        try {
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}