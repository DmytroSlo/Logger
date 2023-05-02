import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String logFileRead = "C:\\Program Files (x86)\\Sky\\ETHOS\\Logs\\ComDLL_ErrorLog.txt";

        File file = new File(logFileRead);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        StringBuilder content = new StringBuilder();
        String line;

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        boolean started = false;

        while (true) {
            if ((line = reader.readLine()) != null) {
                if (line.contains("System.Net.Sockets.Socket.Bind") || line.contains("#3418")) {
                    startTime = LocalDateTime.now();
                    started = true;
                } else if (line.contains("w CommunicationProtocol.CommunicationManager.Initialise(String ipAddress, UInt32 portNo)") || line.contains("Fixed")) {
                    if (started) {
                        endTime = LocalDateTime.now();
                        Duration duration = Duration.between(startTime, endTime);
                        String time = String.format("Error: " + line + "\nTime start: %s, Time end: %s, Diff: %s\n",
                                startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                formatDuration(duration));
                        content.append(time);
                        started = false;
                    }
                }
            } else {
                Thread.sleep(1000); // чекати 1 секунду, якщо файл поки що порожній
            }

            // записувати вміст файлу кожні 10 помилок
            if (content.length() >= 1) {
                FileWriter save = new FileWriter("src\\ErrorLog\\Error.txt", true); // додати до кінця файлу
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
