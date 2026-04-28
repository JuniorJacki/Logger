import de.juniorjacki.utils.Logger;
import de.juniorjacki.utils.logger.Configuration;
import de.juniorjacki.utils.logger.FileWriter;

import java.io.IOException;
import java.nio.file.Path;

public class TestLogger {

    public static void main(String[] args) {
        try {
            FileWriter.enable("logs");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Logger.info("Ente","Banane");
    }
}
