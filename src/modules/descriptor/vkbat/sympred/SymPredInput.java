package modules.descriptor.vkbat.sympred;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import system.SwiPred;
import utilities.LocalToolBase;

/**
 * 
 * @author Rand Strauss
 * @editor Benjamin Strauss
 *
 */

public class SymPredInput extends LocalToolBase {
    static String JOBNAME_FIELD = "mbjob[description]";
    static String EMAIL_FIELD = "email";
    static String SYMPRED_FIELD = "sympred";
    static String SYMPRED_CONTENT = "Do prediction";
    static String SEQUENCE_DATA = "seq";
    static String SEQUENCE_FILE = "seq_file";

    String seq_file;
    String jobName;

    static final String FMT = "yyMMdd-HHmm";
    static final SimpleDateFormat DATE_FMT = new SimpleDateFormat(FMT);

    SymPredInput(String data) {
        seq_file = data;
        jobName = "SwiPred-SymPred-Job-" + DATE_FMT.format(new Date());
    }

    public String getJobName() {
        return jobName;
    }

    class MPH extends MultiPartHandler {
        MPH(HttpURLConnection http) throws IOException {
            super(http);
        }

        @Override public void addParts() throws IOException {
            addPartString(SEQUENCE_DATA, seq_file);
            addEmptyFile(SEQUENCE_FILE);
            addPartString(JOBNAME_FIELD, jobName);
            for (final CannedField field: CannedField.values()) {
                addPartString(field.property, field.value);
            }
            
            if(SwiPred.getProject() != null) {
            	addPartString(EMAIL_FIELD, SwiPred.getProject().email());
            } else {
            	addPartString(EMAIL_FIELD, "");
            }
            
            addPartString(SYMPRED_FIELD, SYMPRED_CONTENT);
        }
    }


    public void submitMultipartContent(HttpURLConnection http) throws IOException {
        final MPH mph = new MPH(http);
        mph.withSummary(10 + seq_file.length());
        mph.submit();
        //dqp("SymPredInput: "+mph.getStats());
       // dqp("SymPredInput: "+mph.getSummary());
    }

    public String getJson() {
        // Note: the order of elements in a JSON string doesn't matter
        final StringBuilder sb = new StringBuilder(300 + seq_file.length());
        sb.append("{\n");
        addJobName(sb);
        for (final CannedField field: CannedField.values()) {
            add(sb, field);
        }
        addDataAtEnd(sb);
        return sb.toString();
    }

    void addJobName(StringBuilder sb) {
        add(sb, JOBNAME_FIELD, jobName, false);
    }

    void addDataAtEnd(StringBuilder sb) {
        add(sb, SYMPRED_FIELD, SYMPRED_CONTENT, true);
    }

    enum CannedField {
        DATABASE("database","nr"),
        PRED1("pred1","-phdpsi","PHDpsi"),  // we always specify all the SymPred methods
        PRED2("pred2","-prof","PROFsec"),
        PRED3("pred3","-sspro","SSPro 2.01"),
        PRED4("pred4","-predator","Predator"),
        PRED5("pred5","-yaspin","YASPIN"),
        PRED6("pred6","-jnet","JNET"),
        PRED7("pred7","-psipred","PSIPred"),
        CONMETHOD("conmethod","D",""),  // dynamic programming vs M? Majority Voting
        CONWEIGHT("conweight","N");

        String readableName;  // not used
        String property;
        String value;
        CannedField(String property, String cannedValue) {
            this(property, cannedValue, "");
        }
        CannedField(String property, String cannedValue, String readableName) {
            this.property = property;
            this.value = cannedValue;
        }
    }

    void add(StringBuilder sb, CannedField field) {
        add(sb, field.property, field.value, false);
    }

    void add(StringBuilder sb, String name, String value, boolean last) {
        sb.append("  '").append(name).append("':'").append(value).append("'");
        sb.append(last ? "\n}" : ",\n");
    }
}
