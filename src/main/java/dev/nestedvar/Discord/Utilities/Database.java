package dev.nestedvar.Discord.Utilities;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


public class Database {

    private static final MongoClientOptions options = MongoClientOptions.builder().build();
    private static MongoClient client;
    private static MongoDatabase db;

    public static void connect() {
        MongoCredential credentials = MongoCredential.createCredential("Entity", "admin", Constants.get("dbpass").toCharArray());
        client = new MongoClient(new ServerAddress("entity.nestedvar.dev", 42069), credentials, options);
        db = client.getDatabase("Entity");
    }

    public static MongoCollection<Document> getCollection(String collection) {
        return db.getCollection(collection);
    }
    public static void close() {
        client.close();
    }
}
