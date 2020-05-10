package dev.nestedvar.Discord.Utilities;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;

public class Utilities {

    Logging logging = new Logging();
    Database db = new Database();

    public static HashMap<String, String> locales = new HashMap<>();
    public static HashMap<String, String> prefixes = new HashMap<>();
    public static HashMap<String, String> logChannels = new HashMap<>();
    public static HashMap<String, String> loggerMessages = new HashMap<>();

    public static HashMap<String, HashMap<String, String>> settings = new HashMap<String, HashMap<String, String>>();

    //Loads existing guild settings from database
    public void load() {
        db.connect();
        MongoCollection<Document> guilds = db.getCollection("guilds");

        FindIterable<Document> iterable = guilds.find();
        MongoCursor<Document> cursor = iterable.iterator();
        try {
            while (cursor.hasNext()) {
                JSONParser parser = new JSONParser();
                JSONObject obj = (JSONObject) parser.parse(cursor.next().toJson());
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("locale", obj.get("locale").toString());
                data.put("prefix", obj.get("prefix").toString());
                data.put("logChannel", obj.get("logChannelID").toString());
                data.put("joinLog", obj.get("joinLogID").toString());
                data.put("ownerRole", obj.get("ownerRoleID").toString());
                data.put("administratorRole", obj.get("administratorRoleID").toString());
                data.put("moderatorRole", obj.get("moderatorRoleID").toString());
                data.put("helperRole", obj.get("helperRoleID").toString());
                data.put("isPremium", obj.get("isPremium").toString());
                data.put("adminSet", obj.get("adminSetEnabled").toString());

                settings.put(obj.get("guildID").toString(), data);
            }
        } catch (ParseException ex) {
            logging.error(Utilities.class, ex.toString());
        } finally {
            settings.forEach((k, v) -> {
                logging.info(Utilities.class, String.format("Key: %s, Value: %s", k, v));
            });
            cursor.close();
        }

    }

    // Return prefix for guild
    public String getPrefix(Guild guild) {
        HashMap<String, String> map = settings.get(guild.getId());
        return map.get("prefix");
    }


    public void setPrefix(Guild guild, String newPrefix) {
        db.connect();
        MongoCollection<Document> guilds = db.getCollection("guilds");
        guilds.find(eq("guildID", guild.getId())).first().replace("prefix", getPrefix(guild), newPrefix);
        db.close();

        HashMap<String, String> map = settings.get(guild.getId());
        map.put("prefix", newPrefix);
        settings.put(guild.getId(), map);

    }

    // Return locale for guild
    public String getLocale(Guild guild) {
        HashMap<String, String> map = settings.get(guild.getId());
        return map.get("locale");
    }

    // Return default locale
    public String getDefaultLocale(Guild guild) {
        /*if (guild.getRegionRaw().equals("us-east")) return "en_US";
        else if (guild.getRegionRaw().equals("us-west")) return "en_US";
        else if (guild.getRegionRaw().equals("us-central")) return "en_US";
        else if (guild.getRegionRaw().equals("us-south")) return "en_US";
        else if (guild.getRegionRaw().equals("brazil")) return "pt_BR";
        else if (guild.getRegionRaw().equals("europe")) return "en_GB";
        else if (guild.getRegionRaw().equals("india")) return "hi_IN";
        else if (guild.getRegionRaw().equals("hongkong")) return "en_HK";
        else if (guild.getRegionRaw().equals("japan")) return "ja_JP";
        else if (guild.getRegionRaw().equals("russia")) return "ru_RU";
        else if (guild.getRegionRaw().equals("singapore")) return "en_SG";
        else if (guild.getRegionRaw().equals("southafrica")) return "en_ZA";
        else if (guild.getRegionRaw().equals("sydney")) return "en_AU";
        else return "en_US";*/
        switch (guild.getRegionRaw()) {
            case "us-east":
                return "en_US";
            case "us-west":
                return "en_US";
            case "us-south":
                return "en_US";
            case "us-central":
                return "en_US";
            case "brazil":
                return "pt_BR";
            case "europe":
                return "en_GB";
            case "india":
                return "hi_IN";
            case "hongkong":
                return "en_HK";
            case "japan":
                return "ja_JP";
            case "russia":
                return "ru_RU";
            case "singapore":
                return "en_SG";
            case "southafrica":
                return "en_ZA";
            case "sydney":
                return "en_AU";
            default:
                return "en_US";
        }
    }

    // Set locale for guild
    public void setLocale(Guild guild, String locale) {
        db.connect();
        MongoCollection<Document> guilds = db.getCollection("guilds");
        guilds.find(eq("guildID", guild.getId())).first().replace("locale", getLocale(guild), locale);
        db.close();
        HashMap<String, String> map = settings.get(guild.getId());
        map.put("locale", locale);
        settings.put(guild.getId(), map);
    }

    public TextChannel getLogChannel(Guild guild){
        HashMap<String, String> map = settings.get(guild.getId());
        if(map.get("logChannel").equalsIgnoreCase("null")){
            return guild.getDefaultChannel();
        }
        else {
            return  guild.getTextChannelById(map.get("logChannel"));
        }
    }

    public void setLogChannel(Guild guild, String channelID){
        db.connect();
        MongoCollection<Document> guilds = db.getCollection("guilds");
        guilds.find(eq("guildID", guild.getId())).first().replace("logChannelID", getLogChannel(guild), channelID);
        db.close();
        HashMap<String, String> map = settings.get(guild.getId());
        map.put("logChannel", channelID);
        settings.put(guild.getId(), map);
    }

    public TextChannel getJoinLogChannel(Guild guild){
        HashMap<String, String> map = settings.get(guild.getId());
        if(map.get("joinLog").equalsIgnoreCase("null")){
            return guild.getDefaultChannel();
        }
        else {
            return guild.getTextChannelById(map.get("joinLog"));
        }

    }

    public void setJoinLogChannel(Guild guild, String channelID){
        db.connect();
        MongoCollection<Document> guilds = db.getCollection("guilds");
        guilds.find(eq("guildID", guild.getId())).first().replace("joinLogID", getLogChannel(guild), channelID);
        db.close();
        HashMap<String, String> map = settings.get(guild.getId());
        map.put("joinLog", channelID);
        settings.put(guild.getId(), map);
    }

    public Role getOwnerRole(Guild guild){
        HashMap<String, String> map = settings.get(guild.getId());
        return guild.getRoleById(map.get("ownerRole"));
    }

    public void setOwnerRole(Guild guild, String roleID){
        db.connect();
        MongoCollection<Document> guilds = db.getCollection("guilds");
        guilds.find(eq("guildID", guild.getId())).first().replace("ownerRoleID", getAdministratorRole(guild), roleID);
        db.close();
        HashMap<String, String> map = settings.get(guild.getId());
        map.put("ownerRole", roleID);
        settings.put(guild.getId(), map);
    }

    public Role getAdministratorRole(Guild guild){
        HashMap<String, String> map = settings.get(guild.getId());
        return guild.getRoleById(map.get("administratorRole"));
    }

    public void setAdministratorRole(Guild guild, String roleID){
        db.connect();
        MongoCollection<Document> guilds = db.getCollection("guilds");
        guilds.find(eq("guildID", guild.getId())).first().replace("administratorRoleID", getAdministratorRole(guild), roleID);
        db.close();
        HashMap<String, String> map = settings.get(guild.getId());
        map.put("administratorRole", roleID);
        settings.put(guild.getId(), map);
    }

    public Role getModeratorRole(Guild guild){
        HashMap<String, String> map = settings.get(guild.getId());
        return guild.getRoleById(map.get("administratorRole"));
    }

    public void setModeratorRole(Guild guild, String roleID){
        db.connect();
        MongoCollection<Document> guilds = db.getCollection("guilds");
        guilds.find(eq("guildID", guild.getId())).first().replace("moderatorRoleID", getModeratorRole(guild), roleID);
        db.close();
        HashMap<String, String> map = settings.get(guild.getId());
        map.put("moderatorRole", roleID);
        settings.put(guild.getId(), map);
    }

    public boolean isAdminEnabledForSet(Guild guild){
        HashMap<String, String> map = settings.get(guild.getId());
        if(map.get("adminSet").equals("true")){
            return true;
        } else return false;
    }

    public String getSelfAvatar(GuildMessageReceivedEvent event){
        return event.getJDA().getSelfUser().getEffectiveAvatarUrl();
    }

    public boolean isGuildAlreadyAvailable(Guild guild){
        HashMap<String, String> map = settings.get(guild.getId());
        if(map.isEmpty() || map.toString() == null) return true;
        else return false;
    }

}
