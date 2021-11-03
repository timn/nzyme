package horse.wtf.nzyme.alerts.service.callbacks;

import horse.wtf.nzyme.alerts.BeaconRateAnomalyAlert;
import horse.wtf.nzyme.alerts.ProbeFailureAlert;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.*;

public class ProcessCallbackTest {

    @Test
    public void testCallback() throws IOException {
        if (System.getProperty("os.name").startsWith("Windows")) {
            return;
        }
        Path current_path = Paths.get(System.getProperty("user.dir"));
        String command = Paths.get(current_path.toString(), "src", "test", "resources", "test.sh").toString();
        ProcessCallback callback = new ProcessCallback(ProcessCallback.Configuration.create(command, 5));
        callback.call(ProbeFailureAlert.create(DateTime.now(), "fooProbe", "is broke"));
    }

}
