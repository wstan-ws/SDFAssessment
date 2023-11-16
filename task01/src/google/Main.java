package google;

public class Main {

    public static void main(String[] args) throws Exception {

        if (args.length <= 0) {
            System.err.print("No file provided");
            System.exit(1);
        } 

        String file = args[0];

        Process process = new Process();

        process.start(file);

    }
}