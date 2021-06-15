package codigo;

import java.io.FileWriter;
import java.io.PrintWriter;

public class Log {
    //PRIVATES VARIABLES
    //------------------------------------------------------------------------------------------------------------------
    private final String REPORT_FILE_NAME = "log.txt";

    //CONSTRUCTOR
    //------------------------------------------------------------------------------------------------------------------
    public Log(){

    }

    //PUBLIC METHODS
    //------------------------------------------------------------------------------------------------------------------
    public void registrarDisparo(String transicion) {

        /*String tittle = "##########################################\n" +
                new Date() + "\n" +
                "REPORT OF PROGRAM \n" +
                "Thirty Threads\n" +
                "##########################################";*/

        //write a tittle in a file
        this.writeFile(transicion);

    }

    //PRIVATE METHODS
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @return [String] report
     * @apiNote Print a information log
     */

   /* private String takeInformation() {
        String report = "";

        //Save report
        report += ("----------------------------------\n");

        report += ("Report " + this.reportNumber +" |\n"+ "----------" +
                bookCase.getBooksStats() +
                "\n");
        report += ("----------------------------------\n");

        //increase a report number counter
        this.reportNumber++;

        //print report
        System.out.printf(report);

        return report;
    }*/

    /**
     * @param report [String]
     * @apiNote write report in a file
     */
    private void writeFile(String report) {
        FileWriter file = null;
        PrintWriter pw = null;
        try {
            file = new FileWriter(REPORT_FILE_NAME, true);
            pw = new PrintWriter(file);

            pw.println(report);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != file)
                    file.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

}
