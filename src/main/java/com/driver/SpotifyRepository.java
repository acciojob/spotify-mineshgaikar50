package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<String> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<String>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name,mobile);
        users.add(name);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        artistAlbumMap.put(artist, new ArrayList<>());
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Album album = new Album(title);
        albums.add(album);
        for (Artist artist : artists) {
            if (artist.getName().equals(artistName)) {
                artistAlbumMap.get(artist).add(album);
                albumSongMap.put(album, new ArrayList<>());
                return album;
            }
        }
        return null;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Song song = new Song(title, length);
        songs.add(song);
        for (Album album : albums) {
            if (album.getTitle().equals(albumName)) {
                albumSongMap.get(album).add(song);
                songLikeMap.put(song, new ArrayList<>());
                return song;
            }
        }
        throw new Exception("Album not found");
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        for (Song song : songs) {
            if (song.getLength() <= length) {
                playlistSongMap.putIfAbsent(playlist, new ArrayList<>());
                playlistSongMap.get(playlist).add(song);
            }
        }
        for (User user : getUserObjects()) {
            if (user.getMobile().equals(mobile)) {
                creatorPlaylistMap.put(user, playlist);
                userPlaylistMap.putIfAbsent(user, new ArrayList<>());
                userPlaylistMap.get(user).add(playlist);
                return playlist;
            }
        }
        throw new Exception("User not found");

    }



    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        for (String songTitle : songTitles) {
            for (Song song : songs) {
                if (song.getTitle().equals(songTitle)) {
                    playlistSongMap.putIfAbsent(playlist, new ArrayList<>());
                    playlistSongMap.get(playlist).add(song);
                }
            }
        }
        for (User user : getUserObjects()) {
            if (user.getMobile().equals(mobile)) {
                creatorPlaylistMap.put(user, playlist);
                userPlaylistMap.putIfAbsent(user, new ArrayList<>());
                userPlaylistMap.get(user).add(playlist);
                return playlist;
            }
        }
        throw new Exception("User not found");

    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        for (User user : getUserObjects()) {
            if (user.getMobile().equals(mobile)) {
                for (Playlist playlist : userPlaylistMap.getOrDefault(user, new ArrayList<>())) {
                    if (playlist.getTitle().equals(playlistTitle)) {
                        return playlist;
                    }
                }
            }
        }
        throw new Exception("Playlist not found");

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = null;
        Song song = null;

        for (User u : getUserObjects()) {
            if (u.getMobile().equals(mobile)) {
                user = u;
                break;
            }
        }

        if (user == null) {
            throw new Exception("User not found");
        }

        for (Song s : songs) {
            if (s.getTitle().equals(songTitle)) {
                song = s;
                break;
            }
        }

        if (song == null) {
            throw new Exception("Song not found");
        }

        List<User> likedUsers = songLikeMap.get(song);
        if (!likedUsers.contains(user)) {
            likedUsers.add(user);
            song.setLikes(song.getLikes() + 1);
        }
        return song;

    }

    public String mostPopularArtist() {
        int maxLikes = 0;
        Artist mostPopularArtist = null;

        for (Artist artist : artists) {
            int totalLikes = 0;
            List<Album> albumsByArtist = artistAlbumMap.get(artist);

            for (Album album : albumsByArtist) {
                List<Song> songsInAlbum = albumSongMap.get(album);

                for (Song song : songsInAlbum) {
                    totalLikes += song.getLikes();
                }
            }

            if (totalLikes > maxLikes) {
                maxLikes = totalLikes;
                mostPopularArtist = artist;
            }
        }

        return mostPopularArtist != null ? mostPopularArtist.getName() : null;
    }

    public String mostPopularSong() {
        int maxLikes = 0;
        Song mostPopularSong = null;

        for (Song song : songs) {
            if (song.getLikes() > maxLikes) {
                maxLikes = song.getLikes();
                mostPopularSong = song;
            }
        }

        return mostPopularSong != null ? mostPopularSong.getTitle() : null;
    }

    User[] getUserObjects() {
        // Assuming you have a way to create User objects from the 'users' list.
        // Here we are just creating new User objects with dummy mobile numbers.
        User[] userObjects = new User[users.size()];
        for (int i = 0; i < users.size(); i++) {
            userObjects[i] = new User(users.get(i), "0000000000");
        }
        return userObjects;
    }
}
