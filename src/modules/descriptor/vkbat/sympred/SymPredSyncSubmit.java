package modules.descriptor.vkbat.sympred;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import assist.util.Pair;
import utilities.LocalToolBase;

/**
 * 
 * @author Rand Strauss
 * @editor Benjamin Strauss
 */

public class SymPredSyncSubmit extends LocalToolBase {

    static final String GET = "POST";
    static final String FASTA_POST_URL = "https://www.ibi.vu.nl/programs/sympredwww/";
    static final int TENK = 10 * 1024;

    int returnStatus = -1;
    String returnMessage;
    String responsePage;

    Pair<HttpStatusCode, String> submitJob(SymPredInput input) {
    	Pair<HttpStatusCode, String> retval = new Pair<HttpStatusCode, String>();
    	
        URL url;
        qp("Submitting job: "+input.jobName);
        try {
            url = new URL(FASTA_POST_URL);
            final HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod(GET);
            http.setDoOutput(true);
            http.setRequestProperty("Accept", "application/json");
            //http.setRequestProperty("Content-Type", "text/plain"); // python
            http.setReadTimeout(minutes(1));
            
            input.submitMultipartContent(http);

            returnStatus = http.getResponseCode();
            returnMessage = http.getResponseMessage();
            
            //dqp("reqbin.com returns: " + returnStatus + ", " + http.getResponseMessage());
            responsePage = getResponsePage(http.getInputStream());
            if ((responsePage != null) && !responsePage.isEmpty()) {
            	retval.y = responsePage;
            }

            http.disconnect();
            retval.x = HttpStatusCode.get(returnStatus);
            return retval;
        } catch (final IOException e) {
            throw new SendException(e);
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
