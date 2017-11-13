import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class Main {

    public static List<Integer> hiddenLayers;
    public static int populationSize;
    public static int offspringCount;
    public static String algorithm;
    public static String dataFilePath;

    private static Network network;
    public static boolean shouldPause = false;
    private static String savePath;

    public static void start(String algorithm, List<Integer> hiddenLayers, int populationSize, int numOfChildren) {
        Logger.log("Starting");

        ReadData.load(dataFilePath);
        List<Example> examples = ReadData.getExamples();

        Evolution.Algorithm chosenAlgorithm = Evolution.Algorithm.GA;

        if (algorithm.equalsIgnoreCase("GA")) {
            chosenAlgorithm = Evolution.Algorithm.GA;
        } else if (algorithm.equalsIgnoreCase("BP")) {
            chosenAlgorithm = Evolution.Algorithm.BP;
        } else if (algorithm.equalsIgnoreCase("ES")) {
            chosenAlgorithm = Evolution.Algorithm.ES;
        } else if (algorithm.equalsIgnoreCase("DE")) {
            chosenAlgorithm = Evolution.Algorithm.DE;
        }

        Evolution evolution = new Evolution(chosenAlgorithm, hiddenLayers, examples, populationSize, numOfChildren);

        Logger.log("Created evolution network");

        Logger.log("Starting to run evolution network");

        evolution.run();
    }

    private static boolean printHelp() {
        // Prints as git table
        // Prints headers
        System.out.format("| %-" + CommandLineParameter.flagLength + "s | " +
                        "%-" + CommandLineParameter.descriptionLength + "s | " +
                        "%-" + CommandLineParameter.defaultLength + "s | " +
                        "%-" + CommandLineParameter.parameterLength + "s |\n",
                "Flag", "Description", "Default", "Parameter");

        // Prints the lines below the header
        System.out.println(String.format("| %-" + CommandLineParameter.flagLength + "s | " +
                "%-" + CommandLineParameter.descriptionLength + "s |:" +
                "%-" + CommandLineParameter.defaultLength + "s:|:" +
                "%-" + CommandLineParameter.parameterLength + "s:|", "", "", "", "")
                .replaceAll(" ", "-"));

        Stream.of(commands).forEach(System.out::println);
        System.exit(0);
        return true;
    }

    private static boolean parseHiddenLayers(String arg) {
        hiddenLayers = new ArrayList<Integer>();
        for (String s : arg.split(",")) {
            hiddenLayers.add(Integer.parseInt(s.trim()));
        }
        return true;
    }

    private static CommandLineParameter[] commands = {
            new CommandLineParameter("-a",     "Algorithm (bp, ga, ds, de)",                                   s -> algorithm = (String) s,            "ga",   CommandLineParameter.Type.String),   // Algorithm
            new CommandLineParameter("-b",     "Beta Rate",                                                    d -> Evolution.beta = (double) d,         .1,   CommandLineParameter.Type.Double),   // Beta Rate
            new CommandLineParameter("-f",     "Data File",                                                    s -> dataFilePath = (String) s,  "iris.data",   CommandLineParameter.Type.String),   // Data File
            new CommandLineParameter("-h",     "Displays the help text",                                       f -> printHelp(),                       null,   CommandLineParameter.Type.Void),     // Help
            new CommandLineParameter("-hl",    "The amount of hidden layers, and the amount of nodes in each", s -> parseHiddenLayers((String) s),  "40,40",   CommandLineParameter.Type.String),   // Hidden Layers
            new CommandLineParameter("-lr",    "Learning Rate",                                                d -> Evolution.learningRate = (double) d,.01,   CommandLineParameter.Type.Double),   // Learning Rate
            new CommandLineParameter("-m",     "Mutation Rate",                                                d -> Evolution.mutationChance = (double) d,.05, CommandLineParameter.Type.Double),   // Mutation Rate
            new CommandLineParameter("-o",     "Offspring count",                                              i -> offspringCount = (int) i,            10,   CommandLineParameter.Type.Integer),  // Offspring Count
            new CommandLineParameter("-p",     "Population size",                                              i -> populationSize = (int) i,            10,   CommandLineParameter.Type.Integer),  // Population Size
            new CommandLineParameter("-s",     "Save the weights to a given output file",                      s -> savePath = (String) s,               "",   CommandLineParameter.Type.String),   // Save
    };

    public static void main(String[] args) {
        try {
            // Init default values
            Stream.of(commands)
                    .parallel()
                    .filter(command -> command.paramType != CommandLineParameter.Type.Void) // Don't adjust types without params
                    .forEach(command -> command.func.apply(command.defaultValue));

            // Read command flags and use them
            for (int i = 0; i < args.length; i++) {
                for (CommandLineParameter command : commands) {
                    if (args[i].equals(command.flag)) {
                        switch (command.paramType) {
                            case Integer: command.func.apply(Integer.parseInt(args[++i])); break;
                            case Double: command.func.apply(Double.parseDouble(args[++i])); break;
                            case String: command.func.apply(args[++i]); break;
                            case Void: command.func.apply(0); break;
                        }
                    }
                }
            }
        } catch (NumberFormatException nfe) {
            System.err.println("Invalid arguments");
            System.exit(1);
        }

        start(algorithm, hiddenLayers, populationSize, offspringCount);
        System.exit(0);
    }

    /**
     * Class that represents a command line parameter
     */
    private static class CommandLineParameter {

        public static int flagLength = 6;
        public static int descriptionLength = 60;
        public static int defaultLength = 9;
        public static int parameterLength = 9;
        public Type paramType;
        private String flag;
        private String helpText;
        private Object defaultValue;
        private Function func;

        public CommandLineParameter(String flag, String helpText, Function func, Object defaultValue, Type paramType) {
            this.flag = flag;
            this.helpText = helpText;
            this.func = func;
            this.defaultValue = defaultValue;
            this.paramType = paramType;
        }

        private String toTable(String startFormat, String endFormat) {
            String formatMiddle = "";

            switch (paramType) {
                case Integer: formatMiddle = "%-" + defaultLength + "d"; break;
                case Double: formatMiddle = "%.3f" + String.format("%" + (defaultLength - 4 - ("" + (int) Math.floor((double) defaultValue)).length()) + "s", ""); break;
                case String: formatMiddle = "%-" + defaultLength + "s"; break;
                case Void: formatMiddle = "%-" + defaultLength + "s"; break;
            }

            return String.format(startFormat + formatMiddle + endFormat, flag, helpText, defaultValue == null ? "" : defaultValue, paramType);
        }

        @Override
        public String toString() {
            // return toTable("%-9s%-52s", "%-12s"); Alternate option
            return toTable("| %-" + flagLength + "s | %-" + descriptionLength + "s | ", " | %-" + parameterLength + "s |");
        }

        public enum Type {
            Integer,
            Double,
            String,
            Void
        }

    }

}
