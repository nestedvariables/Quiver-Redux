package dev.nestedvar.Discord.Listeners;

import dev.nestedvar.Discord.Utilities.Constants;
import dev.nestedvar.Discord.Utilities.Resources;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ready extends ListenerAdapter {

    private final Logger LOGGER = LoggerFactory.getLogger(Ready.class);
    Resources res = new Resources();


    public void onReady(ReadyEvent event) {
        if (event.getJDA().getShardInfo().getShardId() < Integer.parseInt(Constants.get("shardcount")) - 1) {
            LOGGER.info("Shard {} has loaded!", event.getJDA().getShardInfo().getShardId());
            LOGGER.info("Quiver shard {} is ready for use!", event.getJDA().getShardInfo().getShardId());
            LOGGER.info("Loading shard {}", event.getJDA().getShardInfo().getShardId() + 1);
        } else {
            LOGGER.info("Shard {} has loaded!", event.getJDA().getShardInfo().getShardId());
            LOGGER.info("Quiver shard {} is ready for use!", event.getJDA().getShardInfo().getShardId());
            LOGGER.info("Quiver has fully loaded and is ready for use on all guilds!");
            LOGGER.info("Quiver is online using " + res.getRAMUsage() + " MB of RAM");
        }

    }

}
