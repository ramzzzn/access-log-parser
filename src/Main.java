import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int fileCount = 0;
        while (true) {
            System.out.println("Введите путь к файлу: ");
            String path = sc.nextLine();
            File file = new File(path);
            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();
            if (!fileExists) {
                System.out.println("Такого файла не существует");
                continue;
            }
            if (isDirectory) {
                System.out.println("Это путь к папке, а не к файлу");
                continue;
            }
            fileCount++;
            System.out.println("Путь указан верно");
            System.out.println("Это файл номер " + fileCount);
            try {
                FileReader fileReader = new FileReader(path);
                BufferedReader reader = new BufferedReader(fileReader);
                String line;
                Statistics statistics = new Statistics();
                while ((line = reader.readLine()) != null) {
                    int length = line.length();
                    if (length > 1024) {
                        throw new TooLongLineException("В файле содержится строка длиннее 1024 символов. " + "Пожалуйста, исправьте файл и попробуйте снова.");
                    }
                    LogEntry parsedLine = new LogEntry(line);
                    if (parsedLine.isValid()) {
                        statistics.addEntry(parsedLine);
                    }
                }
                System.out.println("Объем часового трафика: " + statistics.getTrafficRate());
                System.out.println("Список несуществующих страниц сайта: " + statistics.getNotExistPages());
                System.out.println("Статистика по операционным системам: " + statistics.getOsStatistics());
                System.out.println("Статистика по браузерам: " + statistics.getBrowsersStatistics());
                System.out.println("Среднее количество посещений пользователями: " + statistics.getAvgUsersVisitsCount());
                System.out.println("Среднее количество ошибочных запросов: " + statistics.getAvgErrorRequestCount());
                System.out.println("Средняя посещаемость одним пользователем: " + statistics.getAvgVisitByUser());
                Map.Entry<Integer, Integer> peakVisitEntry = statistics.getPeakVisitPerSecond();
                int peakEntry = peakVisitEntry.getValue();
                LocalDateTime peakTime = LocalDateTime.ofEpochSecond(peakVisitEntry.getKey(), 0, ZoneOffset.of("+03:00"));
                System.out.println("Пиковая посещаемость в секунду: " + peakEntry + ", время пиковой посещаемости: " + peakTime);
                System.out.println("Список сайтов, cо ссылкой на текущий сайт: " + statistics.getDomainNameList());
                Map.Entry<String, Integer> maxVisitEntry = statistics.getMaxVisitPerUser();
                System.out.println("Максимальное количество посещений: " + maxVisitEntry.getValue() + " от пользователя с IP-адресом: " + maxVisitEntry.getKey());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}