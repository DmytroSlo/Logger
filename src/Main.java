import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String logFileRead = "C:\\Program Files (x86)\\Sky\\ETHOS\\Logs\\ComDLL_ErrorLog.txt"; // Adres do pliku z logami

        File file = new File(logFileRead);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        StringBuilder content = new StringBuilder();
        String line;

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        boolean started = false;

        String[] errorStringStart = {"System.Net.Sockets.Socket.Bind", "#3418", "Problem", "ErrorFatality"}; // Nazwa błędu którze powstają

        String[] errorStringFixed = {"Done", "Fixed"}; // Kluczowi slowa po zakonczeniu problemy

        String res = "";
        for(String el : errorStringFixed) {
            res = el;
        }

        String currentError = "";

        while (true) {
            if ((line = reader.readLine()) != null) {
                for(String el : errorStringStart) {
                    if (line.contains(el)) {
                        startTime = LocalDateTime.now();
                        started = true;
                        currentError = el;
                    } else if (line.contains(res)) {
                        if (started) {
                            endTime = LocalDateTime.now();
                            Duration duration = Duration.between(startTime, endTime);
                            String time = String.format("Error: " + currentError + "\nTime start: %s, Time end: %s, Diff: %s\n",
                                    startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                    endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                    formatDuration(duration) + "\n");
                            content.append(time);
                            started = false;
                        }
                    }
                }
            } else {
                Thread.sleep(1000);
            }

            if (content.length() >= 1) {
                FileWriter save = new FileWriter("src\\ErrorLog\\Error.txt", true);
                save.write(content.toString());
                save.close();
                content = new StringBuilder();
            }
        }
    }

    private static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String formattedDuration = String.format("%02d:%02d:%02d", absSeconds / 3600, (absSeconds % 3600) / 60, absSeconds % 60);
        return seconds < 0 ? "-" + formattedDuration : formattedDuration;
    }
}
