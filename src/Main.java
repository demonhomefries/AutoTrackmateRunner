//java -cp "SingleTrackmateRunnerJava.jar;lib/*" Main 
// String trackmateConfigMapPath = args[0];
// String inputDirectory = args[1];
// String outputDirectory = args[2];
// String tifFile = args[3];

import java.io.IOException;
import java.util.List;

public class Main {
    static {
        net.imagej.patcher.LegacyInjector.preinit();
    }
    public static void main(String[] args){

        TrackmateParameters.Settings settings = TrackmateParameters.getSettings();

        // Test variables, should be acquired by TrackmateParameters
        String testInputDirPath = settings.inputDir;
        String testOutputDirPath = settings.outputDir;
        String trackmateConfigJson = settings.trackmateConfigJSONString;
        boolean searchSubdirs = settings.searchSubdirs;
        // boolean saveXML = settings.saveXML;

        // String testInputDirPath = "C:\\Users\\akmishra\\Desktop\\Test_kinetic_movies";
        // String testOutputDirPath = "C:\\Users\\akmishra\\Desktop\\Testing";
        // String trackmateConfigJson = "C:\\Users\\akmishra\\Desktop\\Batch_Trackmate\\Trackmate_RAM_Crash_fix\\src\\Trackmate_config.json";
        // boolean searchSubdirs = true;

        // Replicate the folder structure
        String stringAutotrackedDir = ReplicateFolderStructure.replicateFolders(testInputDirPath, testOutputDirPath);

        // Sanity check, print out the replicated output folder structure
        System.out.println("stringAutotrackedDir: " + stringAutotrackedDir);

        // Get the tiff files to search
        List<String> tifFileList = FileSearcher.findFilesWithExtensions(
            new String[]{"tif", "tiff"}, 
            testInputDirPath, 
            searchSubdirs
        );


        String classpath = "SingleKalmanTrackerRunner.jar;lib/*";

        for (String tifFile : tifFileList) {
            System.out.println("WATCHDOG: STARTING PROCESS FOR " + tifFile);

            // Command: java -cp "SingleTrackmateRunnerJava.jar;lib/*" Main trackmateConfigMapPath inputDirectory outputDirectory tifFile

            ProcessBuilder processBuilder = new ProcessBuilder(
                "java",
                "-cp", classpath,
                "Main",
                trackmateConfigJson,
                testInputDirPath,
                stringAutotrackedDir,
                tifFile
            );

            // Redirect output and error streams to the console
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);


            try {
                // Start the process
                Process process = processBuilder.start();

                // Wait for the process to complete 
                int exitCode = process.waitFor();
                System.out.println("Process exited with code: " + exitCode);
                if (exitCode != 0){
                    System.exit(exitCode);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }


    }
}
