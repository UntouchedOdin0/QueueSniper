package me.vrekt.queuesniper.guild.register;

import me.vrekt.queuesniper.guild.GuildConfiguration;
import me.vrekt.queuesniper.utility.CheckUtility;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.List;

public class GuildRegisterConfiguration {

    private int setupStep;
    private boolean setup;
    private GuildRegistrationWatcher watcher;

    public GuildRegisterConfiguration(boolean setup) {
        this.setup = setup;
    }

    public GuildRegistrationWatcher getWatcher() {
        return watcher;
    }

    public void setWatcher(GuildRegistrationWatcher watcher) {
        this.watcher = watcher;
    }

    /**
     * @return if this guild is setup or not.
     */
    public boolean isSetup() {
        return setup;
    }

    public void setSetup(boolean setup) {
        this.setup = setup;
    }

    public String checkAndReturnOutput(String userInput, Guild guild, GuildConfiguration configuration) {
        SetupStep step = SetupStep.values()[setupStep];
        if (userInput == null) return step.getInstructions();

        switch (step) {
            case ADMINISTRATOR_ROLE:
                Role controlRole = getRole(guild, userInput);
                if (controlRole == null) return "The role " + userInput + " was not found!";
                configuration.setControlRole(controlRole);
                return getNextStep();
            case ANNOUNCEMENT_ROLE:
                Role announcementRole = getRole(guild, userInput);
                if (announcementRole == null) return "The role " + userInput + " was not found!";
                configuration.setAnnouncementRole(announcementRole);
                return getNextStep();
            case ANNOUNCEMENT_CHANNEL:
                TextChannel announcementChannel = getTextChannel(guild, userInput);
                if (announcementChannel == null) return "The channel " + userInput + " was not found!";
                configuration.setAnnouncementChannel(announcementChannel);
                return getNextStep();
            case CODES_CHANNEL:
                TextChannel codesChannel = getTextChannel(guild, userInput);
                if (codesChannel == null) return "The channel " + userInput + " was not found!";
                configuration.setCodesChannel(codesChannel);
                return getNextStep();
            case VOICE_CHANNEL:
                VoiceChannel countdownChannel = getVoiceChannel(guild, userInput);
                if (countdownChannel == null) return "The channel " + userInput + " was not found!";
                configuration.setCountdownChannel(countdownChannel);
                return getNextStep();
            case COUNTDOWN_TIMEOUT:
                int timeout = CheckUtility.tryParse(userInput);
                if (timeout == -99 || timeout >= 120) return "That is not a valid number or it is out of range.";
                configuration.setTimeout(timeout);
                return getNextStep();
        }
        return "Invalid input";
    }

    /**
     * @return the next step
     */
    private String getNextStep() {
        setupStep++;
        if (setupStep >= SetupStep.values().length) {
            setup = true;
            setupStep = 0;

            watcher.removeListener();
            watcher = null;
            return "QueueSniper is now ready for use! Refer to (.help) for help.";
        }
        return SetupStep.values()[setupStep].getInstructions();
    }

    /**
     * Get a role via its name.
     *
     * @param guild the guild to search in
     * @param name  the name of the role
     * @return the role (if found), null otherwise.
     */
    private Role getRole(Guild guild, String name) {
        List<Role> roles = guild.getRolesByName(name, false);
        if (roles.size() == 0) return null;
        return roles.get(0);
    }

    /**
     * Get a text channel via its name.
     *
     * @param guild the guild to search in
     * @param name  the name of the channel
     * @return the channel (if found), null otherwise.
     */
    private TextChannel getTextChannel(Guild guild, String name) {
        List<TextChannel> channels = guild.getTextChannelsByName(name, false);
        if (channels.size() == 0) return null;
        return channels.get(0);
    }

    /**
     * Get a voice channel via its name.
     *
     * @param guild the guild to search in
     * @param name  the name of the channel
     * @return the channel (if found), null otherwise.
     */
    private VoiceChannel getVoiceChannel(Guild guild, String name) {
        List<VoiceChannel> channels = guild.getVoiceChannelsByName(name, false);
        if (channels.size() == 0) return null;
        return channels.get(0);
    }

    /**
     * Stores instructions and steps.
     */
    private enum SetupStep {
        ADMINISTRATOR_ROLE("Welcome to QueueSniper! To get started, start by typing the name of the role that will be used to control the" +
                " QueueSniper bot."),
        ANNOUNCEMENT_ROLE("Enter the role that will be mentioned when snipe matches are starting."),
        ANNOUNCEMENT_CHANNEL("What channel should QueueSniper use to post announcements and server IDs?"),
        CODES_CHANNEL("What channel will players use to post their server IDs?"),
        VOICE_CHANNEL("What voice channel should QueueSniper use to countdown in?"),
        COUNTDOWN_TIMEOUT("Finally, How long should QueueSniper wait to start counting down after one of the playlist commands is " +
                "executed? (in seconds)");

        final String instructions;

        SetupStep(String instructions) {
            this.instructions = instructions;
        }

        String getInstructions() {
            return instructions;
        }
    }

}