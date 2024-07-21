package modules.descriptor.vkbat.sympred;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SymPredResult {

    static final String GET = "GET";
    static final String FASTA_GET_URL = "https://www.ibi.vu.nl/programs/sympredwww/";
    static final int TENK = 10 * 1024;

    int returnStatus;
    String returnMessage;
    String responsePage;

    int get(SymPredInput input) {
        URL url;
        System.out.println("Submitting job: "+input.jobName);
        HttpURLConnection http = null;
        try {
            url = new URL(FASTA_GET_URL);
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod(GET);
            http.setDoOutput(true);
            http.setRequestProperty("Accept", "application/json");
            //http.setRequestProperty("Content-Type", "text/plain"); // python
            http.setRequestProperty("Content-Type", "application/json");
            http.setReadTimeout(seconds(30));

            final int statusCode = http.getResponseCode();
            returnMessage = http.getResponseMessage();
            responsePage = getResponsePage(http.getInputStream());

            System.out.println("reqbin.com returns: " + statusCode + ", " + http.getResponseMessage());

            http.disconnect();
            return statusCode;
        } catch (final IOException e) {
            returnStatus = -1;
            throw new SendException(e);
        } finally {
            if (http != null)
                http.disconnect();
        }
    }

    private String getResponsePage(InputStream inputStream) {
        final StringBuilder sb = new StringBuilder(TENK);
        try (Scanner scanner = new Scanner(inputStream, "UTF-8")) {
            scanner.useDelimiter("\\n");
            while (scanner.hasNext()) {
                sb.append(scanner.next());
            }
        }
        return sb.toString();
    }


    int seconds(int secs) {
        return secs * 1000;
    }
    int minutes(int min) {
        return min * 60 * 1000;
    }

    static class SendException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        SendException(Exception e) {
            super(e.getMessage() + "; " + e.getClass().getSimpleName(), e);
        }
    }
}
