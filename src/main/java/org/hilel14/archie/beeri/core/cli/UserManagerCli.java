package org.hilel14.archie.beeri.core.cli;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.hilel14.archie.beeri.core.Config;
import org.hilel14.archie.beeri.core.users.User;
import org.hilel14.archie.beeri.core.users.UserManager;

/**
 *
 * @author hilel14
 */
public class UserManagerCli {

    static final Logger LOGGER = LoggerFactory.getLogger(UserManagerCli.class);

    public static void main(String[] args) {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            Config config = new Config();
            CommandLine cmd = parser.parse(options, args);
            String action = cmd.getOptionValue("action").trim();
            User user = createUser(cmd);
            UserManager manager = new UserManager(config, null);
            switch (action) {
                case "create":
                    LOGGER.info("Creating user {}", user.getUsername());
                    manager.createUser(user);
                    break;
                case "update":
                    LOGGER.info("Updating user {}", user.getUsername());
                    manager.updatePassword(user);
                    break;
                default:
                    System.out.println("Invalid action: " + action);
            }
            //System.out.println(DigestUtils.sha512Hex(args[0]));
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("users-admin", options);
            System.exit(1);
        } catch (Exception ex) {
            LOGGER.error(null, ex);
        }
    }

    static User createUser(CommandLine cmd) {
        User user = new User();
        user.setUsername(cmd.getOptionValue("u"));
        user.setPassword(cmd.getOptionValue("p"));
        user.setFullname(cmd.getOptionValue("f"));
        user.setRole(cmd.getOptionValue("r"));
        return user;
    }

    static Options createOptions() {
        Options options = new Options();
        Option option;
        // action
        option = new Option("a", "Action. One of: create, update.");
        option.setLongOpt("action");
        option.setArgs(1);
        option.setRequired(true);
        options.addOption(option);
        // user name
        option = new Option("u", "User name to create or update password for.");
        option.setLongOpt("user-name");
        option.setArgs(1);
        option.setRequired(true);
        options.addOption(option);
        // password
        option = new Option("p", "Password to create or update");
        option.setLongOpt("password");
        option.setArgs(1);
        option.setRequired(true);
        options.addOption(option);
        // full name
        option = new Option("f", "full name (Optional for updates).");
        option.setLongOpt("full-name");
        option.setArgs(1);
        option.setRequired(false);
        options.addOption(option);
        // role
        option = new Option("r", "Role (optional for updates). One of: manager, editor");
        option.setLongOpt("role");
        option.setArgs(1);
        option.setRequired(false);
        options.addOption(option);
        // return
        return options;
    }

}
