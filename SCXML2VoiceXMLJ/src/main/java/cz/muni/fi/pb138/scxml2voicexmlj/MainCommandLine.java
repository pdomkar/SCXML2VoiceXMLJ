package cz.muni.fi.pb138.scxml2voicexmlj;

import cz.muni.fi.pb138.scxml2voicexmlj.srgs.Srgs;
import cz.muni.fi.pb138.scxml2voicexmlj.srgs.SrgsImpl;
import cz.muni.fi.pb138.scxml2voicexmlj.voicexml.ScxmlToVoicexmlConverter;
import cz.muni.fi.pb138.scxml2voicexmlj.voicexml.ScxmlToVoicexmlConverterFactory;
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
import java.util.Map;

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
    private static final String XQUERY_ONLY_DATAMODEL_CHILD =
            "for $d in /scxml/datamodel return $d";

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

    static {
        options = new Options();
        options.addOption(OPTION_INPUT_SHORT,           OPTION_INPUT_LONG,           true, OPTION_INPUT_DESCRIPTION);
        options.addOption(OPTION_OUTPUT_VOICEXML_SHORT, OPTION_OUTPUT_VOICEXML_LONG, true, OPTION_OUTPUT_VOICEXML_DESCRIPTION);
        options.addOption(OPTION_OUTPUT_SRGS_SHORT,     OPTION_OUTPUT_SRGS_LONG,     true, OPTION_OUTPUT_SRGS_DESCRIPTION);

        options.addOption(OPTION_HELP_SHORT, OPTION_HELP_LONG, false, OPTION_HELP_DESCRIPTION);

        parser = new BasicParser();
    }

    /**
     * This method processes given arguments with configured option object.
     * Input file is checked for validity and if ok, the outputs are stored
     * or printed on the screen.
     *
     * @param args Arguments from command line
     * @throws ParseException        if there was an error parsing arguments in args parameter
     * @throws FileNotFoundException if any expected file is missing
     * @throws IOException           if any I/O exception occurred
     * @throws SAXException          if any SAX exception occurred
     * @throws InvalidScxmlException if input file is not valid scxml file
     */
    public static void execute(String[] args) throws ParseException, IOException, SAXException, InvalidScxmlException {
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption(OPTION_HELP_SHORT)) {
            log.info("Showing help");
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp(MainCommandLine.class.getName(), options);
            //end and discard everything else, --help overrides it all
            return;
        }

        String inputFile;
        if (cmd.hasOption(OPTION_INPUT_SHORT)) {
            log.info("Evaluating option " + OPTION_INPUT_SHORT);
            System.out.println("Opening and validating input file");
            inputFile = cmd.getOptionValue(OPTION_INPUT_SHORT);
            validateInputFile(inputFile);
            System.out.println("Input file is valid SCXML file.");
        }
        else {
            log.error("Missing option '" + OPTION_INPUT_LONG + "'");
            System.err.println("Error: Option -" + OPTION_INPUT_SHORT + ",--" + OPTION_INPUT_LONG + " is required. " +
                    "See " + APP_NAME + " -" + OPTION_HELP_SHORT + ",--" + OPTION_HELP_LONG + " for usage.");
            return;
        }

        Map<String, String> grammars;
        if (cmd.hasOption(OPTION_OUTPUT_SRGS_SHORT)) {
            log.info("Evaluating option " + OPTION_OUTPUT_SRGS_SHORT);
            String outputFile = cmd.getOptionValue(OPTION_OUTPUT_SRGS_SHORT);
            grammars = runSRGSComponent(inputFile, outputFile);
        }
        else {
            grammars = runSRGSComponent(inputFile, null);
        }

        GrammarReference references = new BasicGrammarReference(grammars);
        String grammarFileName = references.grammarFile();
        if (grammarFileName != null) {
            File grammarFile = new File(grammarFileName);
            if (!grammarFile.exists() || grammarFile.isDirectory()) {
                throw new FileNotFoundException("Grammar file " + grammarFileName + " is missing");
            }
        }

        if (cmd.hasOption(OPTION_OUTPUT_VOICEXML_SHORT)) {
            log.info("Evaluating option " + OPTION_OUTPUT_VOICEXML_SHORT);
            String outputFile = cmd.getOptionValue(OPTION_OUTPUT_VOICEXML_SHORT);
            runVoiceXMLComponent(inputFile, outputFile, references);
        }
        else {
            runVoiceXMLComponent(inputFile, null, references);
        }
    }

    /**
     * Loads input file and check it if it is valid SCXML. InvalidScxmlException is thrown if it is not valid.
     */
    private static void validateInputFile(String inputFile) throws IOException, SAXException, InvalidScxmlException {
        File xmlInputFile = new File(inputFile);
        File schemaFile = new File(XSD_SCXML);
        String output;

        //validation part
        log.debug("opening file '" + xmlInputFile + "'");
        try (InputStream inputStream = new FileInputStream(xmlInputFile)) {
            Source source = new SAXSource(new InputSource(inputStream));
            log.debug("Creating validator");
            Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(schemaFile);
            Validator validator = schema.newValidator();
            try {
                log.info("validating input file '" + xmlInputFile + "'");
                validator.validate(source);
            } catch (SAXException e) {
                throw new InvalidScxmlException(inputFile + "' is not valid SCXML file: " + e.getLocalizedMessage());
            }
        }
    }

    /**
     * Calls the component for generating SRGS output and stores it into given file.
     */
    private static Map<String, String> runSRGSComponent(String inputFile, String outputFile) throws IOException {
        //TODO the rest (not only references, but also creating file?)
        Srgs component = new SrgsImpl();
        Map<String, String> retval;
        try (InputStream is = new FileInputStream(inputFile)) {
            retval = component.getSrgsReferences(is);
        }
        return retval;
    }

    /**
     * Calls the component for generating VoiceXML output and stores it into given file.
     */
    private static void runVoiceXMLComponent(String inputFile, String outputFile, GrammarReference grammars) throws IOException {
        ScxmlToVoicexmlConverterFactory factory = new ScxmlToVoicexmlConverterFactory();
        ScxmlToVoicexmlConverter component = factory.createConverter();
        String vxml;
        try (InputStream is = new FileInputStream(inputFile)) {
            vxml = component.convert(is, grammars);
        }

        OutputStream os;
        if (outputFile != null) {
            os = new FileOutputStream(outputFile);
        }
        else {
            os = System.out;
        }
        try {
            os.write(vxml.getBytes());
            os.flush();
        } finally {
            os.close();
        }
    }
}















