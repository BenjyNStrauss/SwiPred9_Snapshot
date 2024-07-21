package modules.descriptor.vkbat.sympred;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

/**
 * Subclass this, implementing addParts() to output your form fields.
 * <p>If you have a java.net.HttpURLConnection or HttpsURLConnection, you can do:
 * <p>String msg = new MultiPartHandler(http) {
 * <br>&nbsp; &nbsp; @Override addParts() {
 * <br>&nbsp; &nbsp; &nbsp; &nbsp; addPart("field1", "content1");
 * <br>&nbsp; &nbsp; &nbsp; &nbsp; addPart("field2", "content2");
 * <br>&nbsp; &nbsp; }
 * <br>}.submit().getStats();
 * <p>If you have a different kind of object, you must set your content type using getContentType();
 *
 * @author Rand Strauss
 */
abstract class MultiPartHandler {
    private static final Random rand = new Random();

    private OutputStream out;
    private boolean isFirstPart = true;  // the first part doesn't need to start with a newline
    private final StringBuilder tmpSb;   // used by
    private StringBuilder summarySB;
    private int numSummaryBytesPerField;
    private int totalByteCount;
    private int partCount;
    private boolean ended;  // to ensure you don't use this again after calling submit();

    private String boundary;

    private static final long TOP_RAND = 1L << 40;
    private static final long MASK = (TOP_RAND - 1);
    private static final String TWO_DASH = "--";
    private static final String BOUNDARY_DASH = "---------------------------"; // 26 dashes
    private static final String CONTENT_TYPE = "multipart/form-data; boundary=";
    private static final String CONTENT_HDR_NAME_EQ = "Content-Disposition: form-data; name=";

    /**
     * Implement this method to call these to insert your content:
     * <ul>
     *  <li>addPart(String fieldName, Object content);
     *  <li>addPart(String fieldName, boolean newlinesBetween, Object...content);
     * </ul>
     * And at the end call:  finish()
     * @throws IOException
     */
    protected abstract void addParts() throws IOException;


    /**
     * If you use this constructor:
     * <br>&nbsp; &nbsp; MultiPartHandler mph = new MultiPartHandler();
     * <br>you'll have to set up your Content-Type yourself, like:
     * <br>&nbsp; &nbsp; http.setRequestProperty("Content-Type", mph.getContentType());
     * <p>Note: Call withSummary() if afterwards you want to get the actual content sent with: getSummary()
     * <p>After the connection is all set up, call setOutputStream(...),
     * <br>Then call submit() to send the first part of the output, call your addParts() method,
     * and then complete the MultiPart output.
     */
    MultiPartHandler() {
        setupBoundary();
        this.tmpSb = new StringBuilder(1000);
    }

    /**
     * Only call this if you used don't have an Http(s)URLConnection,
     * so you used the MultiPartHandler() constructor.
     * Call it just before you call submit().
     * <p>Note that
     * @param outputStream  e.g. the HttpURLConnection http.getOutputStream()
     */
    public void setOutputStream(OutputStream outputStream) {
        this.out = outputStream;
    }


    /**
     * If your subclass uses this constructor, it sets the Content-Type for you.
     * <p>Note: Call withSummary() if afterwards you want to log getSummary()
     * @param outputStream  the HttpURLConnection http.getOutputStream()
     */
    protected MultiPartHandler(HttpURLConnection http) throws IOException {
        setupBoundary();  // this needs to be called first, to set up the boundary specified in the content type
        http.setRequestProperty("Content-Type", getContentType());  // as soon as this is called, the connection's open
        this.out = http.getOutputStream();
        this.tmpSb = new StringBuilder(1000);
    }

    /**
     * If your subclass uses this constructor, it sets the Content-Type for you.
     * <p>Note: Call withSummary() if afterwards you want to log getSummary()
     * @param outputStream  the HttpURLConnection http.getOutputStream()
     */
    protected MultiPartHandler(HttpsURLConnection https) throws IOException {
        setupBoundary();  // this needs to be called first, to set up the boundary specified in the content type
        https.setRequestProperty("Content-Type", getContentType());  // this must be called before the connection is open
        this.out = https.getOutputStream();  // this opens the the connection
        this.tmpSb = new StringBuilder(1000);
    }


    /**
     * Configures this to save the message, plus up to 100 chars of content per part.
     * @return this object, so you can do, e.g.:
     * <br>String summary = mph.withSummary().submit();
     */
    public void withSummary() {
        withSummary(77);
    }


    /**
     * Configures this to save the message with 40 or more chars of content per part.
     * The summary will contain the content-type definition, which is 100 chars
     * long, and all the part dividers, so there's point making this too small.
     * <p>Binary data is save as 2 hex chars per byte.
     * <p>It shouldn't be too large- you don't want huge files being output to
     * your log file...
     * @return this object, so you can do, e.g.:
     * <br>String summary = mph.withSummary(300).submit();
     */
    public void withSummary(int stringLengthPerField) {
        ensureNotEnded();
        if (stringLengthPerField > 40)
            numSummaryBytesPerField = stringLengthPerField;
        this.summarySB = new StringBuilder(64 * 1000);
    }


    /**
     * If you make this object with just an OutputStream, you'll need to set the content type to:
     * <br>multipart/form-data; boundary=--[boundary]
     * <p>If you make this object with an HttpURLConnection or HttpsURLConnection,
     * we do that for you.
     * @return content
     */
    public String getContentType() {
        return CONTENT_TYPE + boundary;
    }


    /**
     * Use this to add a file entry with the data returned by an InputStream;
     * @param fieldName  the form field's name
     * @param filename  the name of the file
     * @param instream  a byte stream to read bytes from.  It is closed at the end.
     * @throws IOException
     */
    public void addFile(String fieldName, String filename, InputStream instream) throws IOException {
        contentStart(fieldName, filename);
        int count = 0;
        setupByteNames();
        numContentCharsLeft -= 20;  // leave room for the expected count message
        while (instream.available() > 0) {
            final int b = instream.read();
            outWrite(b);
            count++;
            sumWrite(byteNames[b]);
        }
        if (numContentCharsLeft <= 0) {
            summarySB.append(' ').append('(').append(count).append(" bytes total)");
        }
        instream.close();
    }


    /**
     * Use this to add an empty file part.
     * @param fieldName  the form field's name
     * @throws IOException
     */
    public void addEmptyFile(String fieldName) throws IOException {
        contentStart(fieldName, "");
    }


    /**
     * Use this to add a part consisting of a String, boolean, or number.
     * @param fieldName  the form field's name
     * @param content  content.toString() is the content added in this part.
     * @throws IOException
     */
    public void addPartString(String fieldName, Object content) throws IOException {
        contentStart(fieldName);
        final String s = content.toString();
        outWrite(s);
        sumWrite(s);
    }


    /**
     * Use this to add a part consisting of Strings, booleans, or numbers.
     * @param fieldName  the form field's name
     * @param separator  what to put between content objects, e.g. a space or newline, or an empty string for nothing.
     * @param content    each object's toString() is added.
     * @throws IOException
     */
    public void addPart(String fieldName, String separator, Object...contents) throws IOException {
        final byte[] sepBytes = separator.getBytes(StandardCharsets.UTF_8);
        contentStart(fieldName);
        boolean first = true;
        for (final Object content: contents) {
            if (first) {
                first = false;
            } else {
                outWrite(sepBytes);
                sumWrite(separator);
            }
            final String s = content.toString();
            outWrite(s);
            sumWrite(s);
        }
    }


    /**
     * Use this to add a file entry with the data returned by content.toString();
     * @param fieldName  the form field's name
     * @param filename  the name of the file
     * @param content  content.toString() is the content added in this part.
     * @throws IOException
     */
    public void addFile(String fieldName, String filename, Object content) throws IOException {
        contentStart(fieldName);
        final String s = content.toString();
        outWrite(s);
        sumWrite(s);
    }


    /**
     * This sets up the beginning of the multipart form,
     * then calls your addParts() method, and then completes the message.
     * <p>After this is called, you can only interrogate the
     * @return
     * @throws IOException
     */
    public void submit() throws IOException {
        ensureNotEnded();
        addParts();
        endMessage();
        ended = true;
    }


    // ==== for getting information and summary about the message:


    /** @return the number of parts output so far */
    public int getPartCount() {
        return partCount;
    }

    /** @return the number of bytes output so far */
    public int getByteCount() {
        return totalByteCount;
    }


    /**
     * This can only be called after submit() is called.
     * @return a message:  MultiPartHandler output %d bytes in %d parts
     */
    public String getStats() {
        ensureEnded();
        return String.format("MultiPartHandler output %d bytes in %d parts", totalByteCount, partCount);
    }

    /**
     * This can only be called after submit() is called.
     * @return the multi-line content sent, except has a limited string length for each part's content
     * or null if you did not call withSummary() or withSummary(stringLengthPerField)
     */
    public String getSummary() {
        ensureEnded();
        return (summarySB == null) ? null : summarySB.toString();
    }


    // ========== Private methods


    private void ensureEnded() {
        if (!ended)
            throw new RuntimeException("You must call submit() before getting Stats or Summary");
    }


    private void ensureNotEnded() {
        if (ended)
            throw new RuntimeException("After calling submit(), you can only get Stats or Summary");
    }


    private void setupBoundary() {
        final long num1 = TOP_RAND + (rand.nextLong() & MASK);
        final long num2 = TOP_RAND + (rand.nextLong() & MASK);
        boundary = new StringBuilder(60).append(BOUNDARY_DASH).append(num1).append(num2).toString();
    }


    /**
     * Convenience method to write 1 byte to the MultiPartForm
     * @throws IOException
     */
    private void outWrite(int b) throws IOException {
        out.write(b);
        totalByteCount ++;
    }


    /**
     * Convenience method to write bytes to the MultiPartForm
     * @throws IOException
     */
    private void outWrite(byte[] bytes) throws IOException {
        out.write(bytes);
        totalByteCount += bytes.length;
    }


    /**
     * Convenience method to write a string to the MultiPartForm
     * @throws IOException
     */
    private void outWrite(String s) throws IOException {
        outWrite(s.getBytes());
    }


    /**
     * If this isn't the first part, completes the previous part with a newline.
     * <br>Then adds the boundary line,
     * <br>then the content header line with the field name
     * <br>then a blank line
     */
    private void contentStart(String fieldName) throws IOException {
        contentStart(fieldName, null);
    }


    /**
     * If this isn't the first part, completes the previous part with a newline.
     * <br>Then adds the boundary line,
     * <br>then the content header line with the field name
     * <br>and if filename==null, then a blank line
     * <p>If filename != null, it adds to the line: "; filename="filename"
     * <br>and on the next line:  Content-Type: application/octet-stream
     * <br>To make a blank entry, use an empty filename and don't add any content.
     */
    private void contentStart(String fieldName, String filename) throws IOException {
        ensureNotEnded();
        partCount++;
        summaryResetForContent();

        tmpSb.setLength(0); // clear
        if (!isFirstPart) {
            tmpSb.append('\n');
        }
        tmpSb.append(TWO_DASH).append(boundary).append('\n');
        tmpSb.append(CONTENT_HDR_NAME_EQ).append('"').append(fieldName).append('"');

        if (filename != null) {
            tmpSb.append("; filename=\"").append(filename).append("\"\nContent-Type: application/octet-stream");
        }
        tmpSb.append('\n').append('\n');

        final String nonContent = tmpSb.toString();
        outWrite(nonContent);
        if (null != summarySB) {
            summarySB.append(isFirstPart ? "\n" : "").append(nonContent);
        }
        isFirstPart = false;
    }


    private void endMessage() throws IOException {
        tmpSb.setLength(0); // clear
        final String s = tmpSb.append('\n').append(TWO_DASH).append(boundary).append(TWO_DASH).toString();
        outWrite(s);
        if (null != summarySB) {
            summarySB.append(s);
        }
    }


    private static final String ELLIPSES = "...";

    static String[] byteNames;

    private void setupByteNames() {
        if (byteNames != null)
            return;
        byteNames = new String[256];
        for (int i = 0;  i < 16;  i++) {
            byteNames[i] = '0' + Integer.toHexString(i);
        }
        for (int i = 16;  i < 256;  i++) {
            byteNames[i] = Integer.toHexString(i);
        }
    }


    private int numContentCharsLeft;

    private void summaryResetForContent() {
        numContentCharsLeft = numSummaryBytesPerField;
    }

    private void sumWrite(String content) {
        if ((summarySB == null) || (numContentCharsLeft < 0))
            return;
        else if (numContentCharsLeft == 0) {
            numContentCharsLeft = -3;
            summarySB.append(ELLIPSES);
        } else if (content.length() <= numContentCharsLeft) {
            summarySB.append(content);
            numContentCharsLeft -= content.length();
        } else {
            summarySB.append(content.substring(0, numContentCharsLeft));
            numContentCharsLeft -= content.length();
            summarySB.append(ELLIPSES);
        }
    }


    /*
     *    POST /some_path HTTP/1.1
    //    Content-Type: multipart/form-data; boundary=---------------------------735323031399963166993862150
    //    Content-Length: 834
    //    -----------------------------735323031399963166993862150
    //    Content-Disposition: form-data; name="text1"
    //
    //    text 123 abc
    //    -----------------------------735323031399963166993862150
    //    Content-Disposition: form-data; name="text2"
    //
    //    xyz
    //    -----------------------------735323031399963166993862150
    //    Content-Disposition: form-data; name="file1"; filename="a.txt"
    //    Content-Type: text/plain
    //
    //    Content of a.txt.
    //
    //    -----------------------------735323031399963166993862150
    //    Content-Disposition: form-data; name="file2"; filename="a.html"
    //    Content-Type: text/html
    //
    //    <!DOCTYPE html><title>Content of a.html.</title>
    //
    //    -----------------------------735323031399963166993862150
    //    Content-Disposition: form-data; name="file3"; filename="starfish.jpg"
    //    Content-Type: image/jpeg
    //
    //    binary data goes here
    //    -----------------------------735323031399963166993862150--
     */
}
