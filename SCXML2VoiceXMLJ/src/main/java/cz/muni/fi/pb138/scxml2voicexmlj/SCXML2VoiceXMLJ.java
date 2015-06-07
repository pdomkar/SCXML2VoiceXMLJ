package cz.muni.fi.pb138.scxml2voicexmlj;

import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Main entrance
 *
 * @author Tomas Valka
 * @version 1.0
 */
public class SCXML2VoiceXMLJ {

    private static final Logger log = LoggerFactory.getLogger(SCXML2VoiceXMLJ.class);

    /**
     * Main method of this utility. All exceptions are caught here and processed.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        log.debug("Application started");
        try {
            //args = new String[]{"-i", "src/main/run-data/resources/registration.scxml", "-s", "src/main/resources/run-data/registration.grxml", "-v", "src/main/resources/run-data/voicexml.vxml"};
            MainCommandLine.execute(args);
        } catch (MissingArgumentException e) {
            log.warn("Missing argument", e);
            System.err.println(e.getLocalizedMessage());
        } catch (ParseException e) {
            log.error("error parsing args", e);
            System.err.println("Error parsing arguments. See log for more information.");
        } catch (FileNotFoundException e) {
            log.warn("File not found", e);
            System.err.println(e.getLocalizedMessage());
        } catch (MalformedURLException e) {
            log.error("This should not happen", e);
            System.err.println("Error when reaching an external source: " + e.getLocalizedMessage());
        } catch (IOException e) {
            log.error("Error I/O Operation", e);
            System.err.print("Input-output error occurred: " + e.getLocalizedMessage());
        } catch (SAXException e) {
            log.error("This should not happen", e);
            System.err.println("Error when parsing an external source: " + e.getLocalizedMessage());
        } catch (InvalidScxmlException e) {
            log.warn("Input file is not valid", e);
            System.err.println(e.getLocalizedMessage());
        } catch (Exception e) {
            log.error("some error occurred", e);
            System.err.println("Error: " + e.getLocalizedMessage());
        }
        log.debug("Application ends");
    }

}
