package codigo;

import java.io.FileWriter;
import java.io.PrintWriter;

public class Log {
    private FileWriter file;
    private PrintWriter pw;
    Log(){
        FileWriter file = null;
        PrintWriter pw = null;
    }
    //PRIVATES VARIABLES
    //------------------------------------------------------------------------------------------------------------------
    private final String REPORT_FILE_NAME = "log.txt";

    //PUBLIC METHODS
    //------------------------------------------------------------------------------------------------------------------
    public void registrarDisparo(String transicion) {
        try {
            file = new FileWriter(REPORT_FILE_NAME, true);
            pw = new PrintWriter(file);
            pw.println(transicion);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != file) {
                    file.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
