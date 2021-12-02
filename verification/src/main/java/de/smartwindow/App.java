package de.smartwindow;

import java.io.*;
import java.io.IOException;
import java.io.InputStream;

import com.sun.net.httpserver.HttpServer;

import org.apache.commons.io.IOUtils;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Base64;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintImageOptions;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import com.machinezoo.sourceafis.FingerprintTransparency;

import java.nio.file.Files;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws IOException {
        ImageImprovement.init();
        // byte[] bytes = Files.readAllBytes(new File("/tmp/exampleProcessed.png").toPath());
        // FingerprintImage image = new FingerprintImage(bytes, new FingerprintImageOptions().dpi(400));
        // FingerprintTemplate template = new FingerprintTemplate(image);

        InetAddress addr = InetAddress.getByAddress(new byte[] { 0, 0, 0, 0 });
        HttpServer server = HttpServer.create(new InetSocketAddress(addr, 8080), 0);
        server.createContext("/upload", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        FingerprintTemplate last;

        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "Got image, ";
            String method = t.getRequestMethod();
            InputStream body = t.getRequestBody();
            String base64 = IOUtils.toString(body, "UTF-8");
            byte[] decoded = Base64.getDecoder().decode(base64.split(",")[1]);
            decoded = ImageImprovement.processImage(decoded);
            File file = new File("/tmp/testimproved.png");
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
                outputStream.write(decoded);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                FingerprintImage image = new FingerprintImage(decoded, new FingerprintImageOptions().dpi(400));
                FingerprintTemplate template = new FingerprintTemplate(image);
                if (last == null) {
                    response = "Added template";
                } else {
                    double score = new FingerprintMatcher(last).match(template);
                    System.out.println(score);
                    response += "Score: " + score;
                }
                last = template;
            } catch (Exception e) {
                e.printStackTrace();
            }
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static SSLSocket createSocket(String host, int port) throws IOException {
        SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault()
                .createSocket(host, port);
        return socket;
    }
}
