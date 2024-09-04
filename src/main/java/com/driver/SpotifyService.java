package com.driver;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class SpotifyService {

    SpotifyRepository spotifyRepository = new SpotifyRepository();

    public User createUser(String name, String mobile) {
        return spotifyRepository.createUser(name, mobile);
    }

    public Artist createArtist(String name) {
        return spotifyRepository.createArtist(name);
    }

    public Album createAlbum(String title, String artistName) {
        return spotifyRepository.createAlbum(title, artistName);
    }

    public Song createSong(String title, String albumName, int length) throws Exception {
        return spotifyRepository.createSong(title, albumName, length);
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        return spotifyRepository.createPlaylistOnLength(mobile, title, length);
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        return spotifyRepository.createPlaylistOnName(mobile, title, songTitles);
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        Playlist playlist = spotifyRepository.findPlaylist(mobile, playlistTitle);

        // If the playlist is found, add the user as a listener if they aren't already
        for (User user : spotifyRepository.getUserObjects()) {
            if (user.getMobile().equals(mobile)) {
                List<User> listeners = spotifyRepository.playlistListenerMap.getOrDefault(playlist, new ArrayList<>());
                if (!listeners.contains(user)) {
                    listeners.add(user);
                    spotifyRepository.playlistListenerMap.put(playlist, listeners);
                }
                return playlist;
            }
        }
        throw new Exception("User not found");
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        Song likedSong = null;
        for (Song song : spotifyRepository.songs) {
            if (song.getTitle().equals(songTitle)) {
                likedSong = song;
                break;
            }
        }

        if (likedSong == null) {
            throw new Exception("Song does not exist");
        }

        User user = null;
        for (User u : spotifyRepository.getUserObjects()) {
            if (u.getMobile().equals(mobile)) {
                user = u;
                break;
            }
        }

        if (user == null) {
            throw new Exception("User does not exist");
        }

        List<User> likers = spotifyRepository.songLikeMap.getOrDefault(likedSong, new ArrayList<>());
        if (!likers.contains(user)) {
            likers.add(user);
            spotifyRepository.songLikeMap.put(likedSong, likers);

            // Automatically like the artist
            for (Artist artist : spotifyRepository.artists) {
                for (Album album : spotifyRepository.artistAlbumMap.get(artist)) {
                    if (spotifyRepository.albumSongMap.get(album).contains(likedSong)) {
                        artist.setLikes(artist.getLikes() + 1);
                    }
                }
            }
        }

        return likedSong;
    }

    public String mostPopularArtist() {
        int maxLikes = 0;
        String popularArtist = "";
        for (Artist artist : spotifyRepository.artists) {
            if (artist.getLikes() > maxLikes) {
                maxLikes = artist.getLikes();
                popularArtist = artist.getName();
            }
        }
        return popularArtist;
    }

    public String mostPopularSong() {
        int maxLikes = 0;
        String popularSong = "";
        for (Song song : spotifyRepository.songs) {
            int songLikes = spotifyRepository.songLikeMap.getOrDefault(song, new ArrayList<>()).size();
            if (songLikes > maxLikes) {
                maxLikes = songLikes;
                popularSong = song.getTitle();
            }
        }
        return popularSong;
    }

    public boolean artistExists(String artistName) {
        return false;
    }

    public boolean albumExists(String albumName) {
        return false;
    }

}

