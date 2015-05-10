package cz.muni.fi.pb138.scxml2voicexmlj;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.net.MalformedURLException;

/**
 * This class is the main entrance to the program. It processes the command line arguments
 * and stores or prints the output file. If any unexpected option is provided the program ends
 * without saving any data and prints an error.
 *
 * @author Wallecnik
 * @version 1.0-SNAPSHOT
 */
public final class MainCommandLine {

    private static final Logger log = LoggerFactory.getLogger(MainCommandLine.class);

    private static final String APP_NAME = "scxml2voicexmlj";
    private static final String XSD_SCXML = "src/main/resources/scxml-schema/scxml.xsd";

    private static final String OPTION_INPUT_SHORT        = "i";
    private static final String OPTION_INPUT_LONG         = "input";
    private static final String OPTION_INPUT_DESCRIPTION  = "Required. Input SCXML file. File must exist.";
    private static final String OPTION_OUTPUT_VOICEXML_SHORT       = "v";
    private static final String OPTION_OUTPUT_VOICEXML_LONG        = "output-voicexml";
    private static final String OPTION_OUTPUT_VOICEXML_DESCRIPTION = "Optional. Output VoiceXML file. File is created if not exist.";
    private static final String OPTION_OUTPUT_SRGS_SHORT       = "s";
    private static final String OPTION_OUTPUT_SRGS_LONG        = "output-srgs";
    private static final String OPTION_OUTPUT_SRGS_DESCRIPTION = "Optional. Output SRGS file. File is created if not exist.";
    private static final String OPTION_HELP_SHORT       = "h";
    private static final String OPTION_HELP_LONG        = "help";
    private static final String OPTION_HELP_DESCRIPTION = "Prints this help.";

    private static final Options           options;
    private static final CommandLineParser parser;

    private static boolean valid = true;

    static {
        options = new Options();
        options.addOption(OPTION_INPUT_SHORT,           OPTION_INPUT_LONG,           true, OPTION_INPUT_DESCRIPTION);
        options.addOption(OPTION_OUTPUT_VOICEXML_SHORT, OPTION_OUTPUT_VOICEXML_LONG, true, OPTION_OUTPUT_VOICEXML_DESCRIPTION);
        options.addOption(OPTION_OUTPUT_SRGS_SHORT,     OPTION_OUTPUT_SRGS_LONG,     true, OPTION_OUTPUT_SRGS_DESCRIPTION);

        options.addOption(OPTION_HELP_SHORT, OPTION_HELP_LONG, false, OPTION_HELP_DESCRIPTION);

        parser = new BasicParser();
    }

    /**
     * Main method of this utility. All exceptions are caught here and processed.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        log.debug("Application started");
        try {
            execute(args);
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
        }/*catch (Exception e) {
            log.error("some error occurred", e);
            System.err.println("Error: " + e.getLocalizedMessage());
        }*/
        System.out.println("Exiting...");
        log.debug("Application ends");
    }

    /**
     * This method processes given arguments with configured option object.
     * Input file is checked for validity and if ok, the outputs are stored
     * or printed on the screen.
     */
    private static void execute(String[] args) throws ParseException, IOException, SAXException {
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption(OPTION_HELP_SHORT)) {
            log.info("Showing help");
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp(MainCommandLine.class.getName(), options);
            return;
        }

        if (cmd.hasOption(OPTION_INPUT_SHORT)) {
            log.info("Evaluating option " + OPTION_INPUT_SHORT);
            System.out.println("Opening and validating input file");
            String file = cmd.getOptionValue(OPTION_INPUT_SHORT);
            loadInputFile(file);
        }
        else {
            log.error("Missing option '" + OPTION_INPUT_LONG + "'");
            System.err.println("Error: Option -" + OPTION_INPUT_SHORT + ",--" + OPTION_INPUT_LONG + " is required. " +
                    "See " + APP_NAME + " -" + OPTION_HELP_SHORT + ",--" + OPTION_HELP_LONG + " for usage.");
            return;
        }

        if (!valid) return;

        if (cmd.hasOption(OPTION_OUTPUT_VOICEXML_SHORT)) {
            log.info("Evaluating option " + OPTION_OUTPUT_VOICEXML_SHORT);
            String file = cmd.getOptionValue(OPTION_OUTPUT_VOICEXML_SHORT);
            storeVoiceXMLOutput(file);
        }
        if (cmd.hasOption(OPTION_OUTPUT_SRGS_SHORT)) {
            log.info("Evaluating option " + OPTION_OUTPUT_SRGS_SHORT);
            String file = cmd.getOptionValue(OPTION_OUTPUT_SRGS_SHORT);
            storeSRGSOutput(file);
        }
    }

    /**
     * Loads input file and check it if it is valid SCXML. FileNotFoundException is thrown if not.
     */
    private static void loadInputFile(String inputFile) throws IOException, SAXException {
        File file = new File(inputFile);
        File schemaFile = new File(XSD_SCXML);

        //validation part
        log.debug("opening file '" + file + "'");
        try (InputStream inputStream = new FileInputStream(file)) {
            Source source = new SAXSource(new InputSource(inputStream));
            log.debug("Creating validator");
            Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaFile);
            Validator validator = schema.newValidator();
            try {
                log.info("validating input file '" + file + "'");
                validator.validate(source);
            } catch (SAXException e) {
                log.warn("Input file '" + file + "' is not valid", e);
                System.err.println("Input file '" + file + "' is not valid SCXML file: " + e.getLocalizedMessage());
                valid = false;
                return;
            }
        }

        System.out.println("Input file is valid SCXML file.");

        //TODO: loading part

    }

    /**
     * Calls the component for generating VoiceXML output and stores it into given file.
     */
    private static void storeVoiceXMLOutput(String outputFile) {
        //TODO: call component and store output
    }

    /**
     * Calls the component for generating SRGS output and stores it into given file.
     */
    private static void storeSRGSOutput(String outputFile) {
        //TODO: call component and store output
    }

}