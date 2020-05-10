package dev.nestedvar.Discord.Utilities;

import dev.nestedvar.Discord.Quiver;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Locale {
    Logging logging = new Logging();
    private String[] locales = {
            "en_US",
            "de_DE",
            "pl_PL"
    };

    public void load() {
        File file = new File("Locale");
        if (file.isDirectory()) {
            try {
                for (String locale : locales) {
                    File verify = new File(String.format("locale/%s.json", locale));
                    if (!verify.isFile()) {
                        InputStream stream = new URL("https://raw.githubusercontent.com/NestedVariables/Quiver-Redux/master/src/main/java/dev/nestedvar/Discord/Locale/" + locale + ".json").openStream();
                        Files.copy(stream, Paths.get("locale/" + locale + ".json"), StandardCopyOption.REPLACE_EXISTING);
                        logging.info(Locale.class, String.format("📂 Regenerated locale file %s", locale));
                    }
                }
            } catch (Exception e) {
                logging.error(Locale.class, e.toString());
            }
        } else {
            try {
                File dir = new File("locale");
                dir.mkdir();
                for (String locale : locales) {
                    InputStream stream = new URL("https://raw.githubusercontent.com/NestedVariables/Quiver-Redux/master/src/main/java/dev/nestedvar/Discord/Locale/" + locale + ".json").openStream();
                    Files.copy(stream, Paths.get("locale/" + locale + ".json"), StandardCopyOption.REPLACE_EXISTING);
                }
                logging.info(Locale.class, "📂 Downloaded locale files");
            } catch (Exception e) {
                logging.error(Locale.class, e.toString());
            }
        }
    }

    public String getLocales(){
        StringBuilder builder = new StringBuilder();
        for (String locale : locales) {
            builder.append(locale.toString() + "\n");
        }
        return builder.toString();
    }

    public String getMessage(Guild guild, String category, String message) {
        JSONParser parser = new JSONParser();
        Utilities utils = new Utilities();
        try {
            File localeFile = Paths.get(String.format("locale/%s.json", utils.getLocale(guild))).toFile();
            InputStream stream = new FileInputStream(localeFile);
            JSONObject object = (JSONObject) parser.parse(IOUtils.toString(stream, "UTF-8"));
            JSONObject jsonCategory = (JSONObject) object.get(category);
            String messageToReturn = (String) jsonCategory.get(message);
            stream.close();
            return messageToReturn;
        } catch (Exception e) {

            try {
                InputStream stream = Quiver.class.getResourceAsStream(String.format("locale/%s.json", utils.getLocale(guild)));
                JSONObject object = (JSONObject) parser.parse(IOUtils.toString(stream, "UTF-8"));
                JSONObject jsonCategory = (JSONObject) object.get(category);
                String messageToReturn = (String) jsonCategory.get(message);
                stream.close();

                logging.warn(Locale.class, String.format("Locale files for '%s' are missing. Using backup locales", utils.getLocale(guild)));

                return messageToReturn;
            } catch (Exception ex) {
                logging.error(Locale.class, e.toString());
                e.printStackTrace();
                logging.error(Locale.class, ex.toString());
                ex.printStackTrace();
                return "{missing locale}";
            }
        }
    }
}
