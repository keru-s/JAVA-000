package java0.nio01;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpClient {
    public static void main(String[] args) {
        HttpGet httpGet = new HttpGet("http://localhost:8808/test");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String result = httpClient.execute(httpGet, httpResponse -> {
                int status = httpResponse.getStatusLine().getStatusCode();
                if (status < 200 || status >= 300) {
                    return "request error";
                }
                HttpEntity entity = httpResponse.getEntity();
                return entity != null ? EntityUtils.toString(entity) : "";
            });
            System.out.println("result = " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
