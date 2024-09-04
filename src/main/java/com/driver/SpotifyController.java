package com.driver;

import java.util.*;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spotify")
public class SpotifyController {

    SpotifyService spotifyService = new SpotifyService();
    HashMap<String, User> userDatabase = new HashMap<>();

    @PostMapping("/add-user")
    public String createUser(@RequestBody User userRequest) {
        String username = userRequest.getName();
        userDatabase.put(username, userRequest);
        return "Success";
    }

    @PostMapping("/add-artist")
    public String createArtist(@RequestParam(name = "name") String name) {
        spotifyService.createArtist(name);
        return "Success";
    }

    @PostMapping("/add-album")
    public String createAlbum(@RequestParam(name = "title") String title, @RequestParam(name = "artistName") String artistName) {
        if (!spotifyService.artistExists(artistName)) {
            spotifyService.createArtist(artistName);
        }
        spotifyService.createAlbum(title, artistName);
        return "Success";
    }

    @PostMapping("/add-song")
    public String createSong(@RequestParam(name = "title") String title, @RequestParam(name = "albumName") String albumName, @RequestParam(name = "length") int length) throws Exception {
        if (!spotifyService.albumExists(albumName)) {
            throw new Exception("Album does not exist");
        }
        spotifyService.createSong(title, albumName, length);
        return "Success";
    }

    @PostMapping("/add-playlist-on-length")
    public String createPlaylistOnLength(@RequestParam(name = "mobile") String mobile, @RequestParam(name = "title") String title, @RequestParam(name = "length") int length) throws Exception {
        if (!userDatabase.containsKey(mobile)) {
            throw new Exception("User does not exist");
        }
        spotifyService.createPlaylistOnLength(mobile, title, length);
        return "Success";
    }

    @PostMapping("/add-playlist-on-name")
    public String createPlaylistOnName(@RequestParam(name = "mobile") String mobile, @RequestParam(name = "title") String title, @RequestParam(name = "songTitles") List<String> songTitles) throws Exception {
        if (!userDatabase.containsKey(mobile)) {
            throw new Exception("User does not exist");
        }
        spotifyService.createPlaylistOnName(mobile, title, songTitles);
        return "Success";
    }

    @PutMapping("/find-playlist")
    public String findPlaylist(@RequestParam(name = "mobile") String mobile, @RequestParam(name = "playlistTitle") String playlistTitle) throws Exception {
        if (!userDatabase.containsKey(mobile)) {
            throw new Exception("User does not exist");
        }
        spotifyService.findPlaylist(mobile, playlistTitle);
        return "Success";
    }

    @PutMapping("/like-song")
    public String likeSong(@RequestParam(name = "mobile") String mobile, @RequestParam(name = "songTitle") String songTitle) throws Exception {
        if (!userDatabase.containsKey(mobile)) {
            throw new Exception("User does not exist");
        }
        spotifyService.likeSong(mobile, songTitle);
        return "Success";
    }

    @GetMapping("/popular-artist")
    public String mostPopularArtist() {
        return spotifyService.mostPopularArtist();
    }

    @GetMapping("/popular-song")
    public String mostPopularSong() {
        return spotifyService.mostPopularSong();
    }
}
