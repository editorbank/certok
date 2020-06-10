package com.github.editorbank.certok;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static org.apache.logging.log4j.LogManager.getLogger;

public class CLI {
    private final Logger log = getLogger(CLI.class);

    static String CMD_HELP = "h";
    static String CMD_ENCRYPT_PASSWORD = "ep";
    static String CMD_CHECK = "c";

    static String KEY_STORAGE = "s";
    static String KEY_ALIAS = "a";
    static String KEY_PASSWORD = "p";
    static String KEY_PASSWORD_ENCCRYPTED = "e";

    private final Options options = (new Options()
            .addOption(CMD_HELP, "help",false,"command for print this Help message.")
            .addOption(CMD_CHECK, "check",false,"command for Check days left of certificate.")
            .addOption(CMD_ENCRYPT_PASSWORD, "encrypt-password",false,"command for Encrypted Password.")
            .addOption(Option.builder(KEY_STORAGE).hasArg().argName("file").desc("file of certificate's Storage.").build())
            .addOption(Option.builder(KEY_PASSWORD).hasArg().argName("pass").desc("Password for storage.").build())
            .addOption(Option.builder(KEY_ALIAS).hasArg().argName("name").desc("Alias of certificate in storage.").build())
            .addOption(KEY_PASSWORD_ENCCRYPTED, "an Encrypted password was used.")
    );

    private void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar certok.jar <command> [oprion [oprion ...]]"
                , " commands and options:", options
                , "");
    }

    public CLI(String[] args) throws IOException {
        try {
            log.trace("app start.");
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(options, args);

            if (line.hasOption(CMD_HELP)) {
                log.trace("command: {}", CMD_HELP);
                help();

            } else if (line.hasOption(CMD_ENCRYPT_PASSWORD)) {
                log.trace("command: {}", CMD_ENCRYPT_PASSWORD);
                String password = line.getOptionValue(KEY_PASSWORD);

                log.debug("password.length(): {}", password.length());
                System.out.println("Encrypted Password: " + new StringCoder().encode(password));

            } else if (line.hasOption(CMD_CHECK)) {
                log.trace("command: {}", CMD_CHECK);
                String storage = line.getOptionValue(KEY_STORAGE);
                String password = line.getOptionValue(KEY_PASSWORD);
                String alias = line.getOptionValue(KEY_ALIAS,null);
                if (line.hasOption(KEY_PASSWORD_ENCCRYPTED)) {
                    password = new StringCoder().decode(password);
                }

                log.debug("keystore: {}", storage);
                log.debug("password.length(): {}", password.length());
                log.debug("alias: {}", alias);
                new Check().checkKeyStore(storage, password, alias);
            } else {
                log.trace("command: {}", CMD_HELP);
                help();

            }
            log.trace("app done successfully.");
        } catch (ParseException exp) {
            log.error("Unexpected exception:" + exp.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            new CLI(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
