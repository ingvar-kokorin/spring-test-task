package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


@RestController
public class PlayerController {
    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    // Get all players
    @GetMapping("/rest/players")
    public ResponseEntity<List<Player>> findAll() {
        return new ResponseEntity<>(playerService.findAll(), HttpStatus.OK);
    }

    // Players count
    @GetMapping("/rest/players/count")
    public ResponseEntity<Long> playerCount() {
        return new ResponseEntity<>(playerService.getCount(), HttpStatus.OK);
    }

    // Get player by id
    @GetMapping("/rest/players/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable long id) {
        if (id == 0) {
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }

        Optional<Player> player = playerService.findById(id);
        return player.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.valueOf(404)));
    }

    // Delete player by id
    @DeleteMapping("/rest/players/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable long id) {
        if (id == 0) {
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }

        if (getPlayerById(id).equals(new ResponseEntity<>(HttpStatus.NOT_FOUND))) {
            return new ResponseEntity<>(HttpStatus.valueOf(404));
        }

        playerService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.valueOf(200));
    }

    @PostMapping(path = "/rest/players/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> createPlayer(@RequestBody Player newPlayer) {

        if (newPlayer.getName() == null || newPlayer.getTitle() == null || newPlayer.getRace() == null
                || newPlayer.getProfession() == null || newPlayer.getBirthday() == null
                || newPlayer.getExperience() == null) {
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }

        if (newPlayer.getName().length() > 12 || newPlayer.getTitle().length() > 30 || newPlayer.getName().isEmpty() ||
                newPlayer.getBirthday().getTime() < 0 || newPlayer.getExperience() > 10000000) {
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }

        newPlayer.setLevel(convertExpToLvl(newPlayer.getExperience()));
        newPlayer.setUntilNextLevel(calculateExpForNextLvl(newPlayer));

        if (newPlayer.getBanned() == null) {
            newPlayer.setBanned(false);
        }

        return new ResponseEntity<>(playerService.savePlayer(newPlayer), HttpStatus.OK);
    }

    private Integer convertExpToLvl(Integer exp) {
        return (int) ((Math.sqrt(2500 + (200 * exp)) - 50) / 100);
    }

    private Integer calculateExpForNextLvl(Player player) {
        int lvl = player.getLevel();
        int exp = player.getExperience();

        return 50 * (lvl + 1) * (lvl + 2) - exp;
    }
}
