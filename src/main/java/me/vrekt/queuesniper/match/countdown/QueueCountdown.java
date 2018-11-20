package me.vrekt.queuesniper.match.countdown;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import me.vrekt.queuesniper.match.countdown.lavaplayer.LavaPlayerSendHandler;
import net.dv8tion.jda.core.audio.SpeakingMode;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class QueueCountdown {

    private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    public QueueCountdown() {
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public void countdown(Guild guild, VoiceChannel channel) {
        final AudioPlayer player = playerManager.createPlayer();
        LavaPlayerSendHandler sendHandler = new LavaPlayerSendHandler(player);

        final AudioManager manager = guild.getAudioManager();
        player.addListener(new AudioEventAdapter() {
            @Override
            public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
                manager.closeAudioConnection();
                player.destroy();
            }
        });
        player.setVolume(50);

        playerManager.loadItem("countdown.mp3", new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                manager.setSpeakingMode(SpeakingMode.PRIORITY);
                manager.setSendingHandler(sendHandler);
                manager.openAudioConnection(channel);

                player.playTrack(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {

            }

            @Override
            public void noMatches() {
                System.out.println("Could not find countdown audio!");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                exception.printStackTrace();
            }
        });

    }
}
