package com.extendedclip.papi.expansion.townychat;

import com.palmergames.bukkit.TownyChat.TownyChatFormatter;
import com.palmergames.bukkit.TownyChat.config.ChatSettings;
import com.palmergames.bukkit.TownyChat.events.AsyncChatHookEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.TownyUniverse;
import me.clip.placeholderapi.expansion.Cleanable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class TownyChatExpansion extends PlaceholderExpansion implements Listener, Cleanable {

  private final String VERSION = getClass().getPackage().getImplementationVersion();

  private Map<String, ChatPlayer> players;

  @Override
  public String getIdentifier() {
    return "townychat";
  }

  @Override
  public String getRequiredPlugin() {
    return "TownyChat";
  }

  @Override
  public String getAuthor() {
    return "clip";
  }

  @Override
  public String getVersion() {
    return VERSION;
  }

  @Override
  public String onPlaceholderRequest(Player p, String identifier) {

    switch (identifier) {
      case "channel_tag":
        return getChatPlayer(p).getTag();
      case "channel_name":
        return getChatPlayer(p).getChannel();
      case "message_color":
        return getChatPlayer(p).getColor();
    }

    try {

      Resident r = TownyUniverse.getInstance().getResident(p.getName());

      switch (identifier) {
        case "world":
          return String.format(ChatSettings.getWorldTag(), new Object[]{p.getWorld().getName()});
        case "town":
          return r.hasTown() ? r.getTown().getName() : "";
        case "townformatted":
          return TownyChatFormatter.formatTownTag(r, false, true);
        case "towntag":
          return TownyChatFormatter.formatTownTag(r, false, false);
        case "towntagoverride":
          return TownyChatFormatter.formatTownTag(r, true, false);
        case "nation":
          return r.hasNation() ? r.getTown().getNation().getName() : "";
        case "nationformatted":
          return TownyChatFormatter.formatNationTag(r, false, true);
        case "nationtag":
          return TownyChatFormatter.formatNationTag(r, false, false);
        case "nationtagoverride":
          return TownyChatFormatter.formatNationTag(r, true, false);
        case "townytag":
          return TownyChatFormatter.formatTownyTag(r, false, false);
        case "townyformatted":
          return TownyChatFormatter.formatTownyTag(r, false, true);
        case "townytagoverride":
          return TownyChatFormatter.formatTownyTag(r, true, false);
        case "title":
          return r.hasTitle() ? r.getTitle() : "";
        case "surname":
          return r.hasSurname() ? r.getSurname() : "";
        case "townynameprefix":
          return r.getNamePrefix();
        case "townynamepostfix":
          return r.getNamePostfix();
        case "townyprefix":
          return r.hasTitle() ? r.getTitle() : r.getNamePrefix();
        case "townypostfix":
          return r.hasSurname() ? r.getSurname() : r.getNamePostfix();
        case "townycolor":
          return r.isMayor() ? ChatSettings.getMayorColour() : r.isKing() ? ChatSettings.getKingColour() : ChatSettings.getResidentColour();
        case "group":
          return TownyUniverse.getInstance().getPermissionSource().getPlayerGroup(p);
        case "permprefix":
          return TownyUniverse.getInstance().getPermissionSource().getPrefixSuffix(r, "prefix");
        case "permsuffix":
          return TownyUniverse.getInstance().getPermissionSource().getPrefixSuffix(r, "suffix");
        case "channeltag":
          return TownyChatFormatter.formatTownyTag(r, Boolean.valueOf(false), Boolean.valueOf(false));
      }

    } catch (NotRegisteredException e) {
      return "";
    }
    return null;
  }

  @Override
  public void cleanup(Player player) {
    if (players != null && players.containsKey(player.getName())) {
      players.remove(player.getName());
    }
  }

  public class ChatPlayer {

    private String player;
    private String channel;
    private String tag;
    private String color;

    public ChatPlayer(String player, String channel, String tag, String color) {
      this.setPlayer(player);
      if (channel != null) {
        this.setChannel(channel);
      } else {
        this.setChannel("");
      }
      if (tag != null) {
        this.setTag(tag);
      } else {
        this.setTag("");
      }
      if (color != null) {
        this.setColor(color);
      } else {
        this.setColor("");
      }
    }

    public String getPlayer() {
      return player;
    }

    public void setPlayer(String player) {
      this.player = player;
    }

    public String getChannel() {
      if (channel == null) {
        return "";
      }
      return channel;
    }

    public void setChannel(String channel) {
      this.channel = channel;
    }

    public String getTag() {
      if (tag == null) {
        return "";
      }
      return tag;
    }

    public void setTag(String tag) {
      this.tag = tag;
    }

    public String getColor() {
      if (color == null) {
        return "";
      }
      return color;
    }

    public void setColor(String color) {
      this.color = color;
    }
  }

  private void updatePlayer(String player, String ch, String tag, String cc) {
    if (players == null) {
      players = new HashMap<String, ChatPlayer>();
    }
    if (players.containsKey(player) && players.get(player) != null) {
      players.get(player).setChannel(ch);
      players.get(player).setTag(tag);
      players.get(player).setColor(cc);
    } else {
      ChatPlayer pl = new ChatPlayer(player, ch, tag, cc);
      players.put(player, pl);
    }
  }

  private ChatPlayer getChatPlayer(Player p) {
    return players != null && players.containsKey(p.getName()) && players.get(p.getName()) != null ?
        players.get(p.getName()) : new ChatPlayer(p.getName(), "", "", "");
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onChat(AsyncChatHookEvent e) {

    if (e.isCancelled()) {
      return;
    }

    Player p = e.getPlayer();
    String tag = "";
    String channel = "";
    String msgColor = "";
    if (e.getChannel() != null) {
      tag = e.getChannel().getChannelTag();
      channel = e.getChannel().getName();
      msgColor = e.getChannel().getMessageColour();
    }

    updatePlayer(p.getName(), channel, tag, msgColor);
  }
}
