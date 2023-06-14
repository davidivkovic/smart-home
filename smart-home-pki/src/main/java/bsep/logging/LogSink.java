package bsep.logging;

import static bsep.util.Utils.RootDir;
import static bsep.util.Utils.coalesce;

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;

import org.bson.Document;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@ApplicationScoped
public class LogSink {

    @Inject Scheduler scheduler;
    @Inject MongoClient db;

    Map<String, Long> bytesRead = new HashMap<>();

    Pattern timestampRegex = Pattern.compile("\"timestamp\":\"([^\"]*)\"");

    public void startup(@Observes StartupEvent se)
    {
        var smartHomePKI = RootDir + "/../smart-home-pki/logs/log.txt";
        var smartHomeDevices = RootDir + "/../smart-home-devices/logs/log.txt";

        cleanFile(smartHomePKI);
        cleanFile(smartHomeDevices);

        scheduler
            .newJob("log-sink")
            .setTask(ctx -> {
                collect("smart-home-pki", smartHomePKI);
                collect("smart-home-devices", smartHomeDevices);
            })
            .setInterval("10s")
            .schedule();
    }

    public void collect(String service, String path)
    {
        String line;
        var jsonLogs = new ArrayList<String>();

        try
        {
            var file = new RandomAccessFile(path, "r");
            file.seek(coalesce(bytesRead.get(service),0L));

            while ((line = file.readLine()) != null) jsonLogs.add(line);

            bytesRead.put(service, file.getFilePointer());
            file.close();

            dbInsert(jsonLogs, service);
        }
        catch (Exception e)
        {
            System.out.println("LogSink failed to batch write logs at " + LocalDateTime.now());
            e.printStackTrace();
        }
    }

    public void dbInsert(List<String> jsonLogs, String service)
    {
        if (jsonLogs.isEmpty()) return;
        var collection = db.getDatabase("smart-home").getCollection("Log");
        var logs = jsonLogs.stream()
                .map(l -> {
                    var matcher = timestampRegex.matcher(l);
                    var timestamp = matcher.find() ? ZonedDateTime.parse(matcher.group(1)) : ZonedDateTime.now();

                    var document = Document
                            .parse(l)
                            .append("service", service)
                            .append("timestamp", timestamp.toLocalDateTime());

                    return new InsertOneModel<>(document);
                })
                .toList();
        collection.bulkWrite(logs, new BulkWriteOptions().ordered(false));
    }

    public void cleanFile(String path) {
        try { new PrintWriter(path).close(); }
        catch (Exception e) {
            System.out.println("LogSink failed to clean file: " + path + " at: " + LocalDateTime.now());
            e.printStackTrace();
        }
    }

}