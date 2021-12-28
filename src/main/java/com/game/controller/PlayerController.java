package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@RestController
public class PlayerController {
    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/rest/players")
    public ResponseEntity<Iterable<Player>> findAll() {
        return new ResponseEntity<>(playerService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/rest/players/count")
    public ResponseEntity<Long> playerCount(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel) {
        List<Player> players = (List<Player>) playerService.findAll();

        if (name != null) {
            players.removeIf(player -> !player.getName().contains(name));
        }

        if (title != null) {
            players.removeIf(player -> !player.getTitle().contains(title));
        }

        if (race != null) {
            players.removeIf(player -> !(player.getRace() == race));
        }

        if (profession != null) {
            players.removeIf(player -> !(player.getProfession() == profession));
        }

        if (after != null) {
            players.removeIf(player -> !(player.getBirthday().after(new Date(after))));
        }

        if (before != null) {
            players.removeIf(player -> !(player.getBirthday().before(new Date(before))));
        }

        if (banned != null) {
            players.removeIf(player -> player.getBanned() != banned);
        }


        if (minExperience != null) {
            players.removeIf(player -> player.getExperience() < minExperience);
        }

        if (maxExperience != null) {
            players.removeIf(player -> player.getExperience() > maxExperience);
        }

        if (minLevel != null) {
            players.removeIf(player -> player.getLevel() < minLevel);
        }

        if (maxLevel != null) {
            players.removeIf(player -> player.getLevel() > maxLevel);
        }

        return new ResponseEntity<>((long) players.size(), HttpStatus.OK);
    }

    @GetMapping("/rest/players/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable long id) {
        if (id == 0) {
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }

        Optional<Player> player = playerService.findById(id);
        return player.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.valueOf(404)));
    }

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

    @PostMapping(path = "/rest/players/")
    public ResponseEntity<Player> createPlayer(@RequestBody Player newPlayer) {

        if (playerService.isRequestContainsNull(newPlayer)) {
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }

        if (!playerService.isRequestMatchCriteria(newPlayer)) {
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }

        int level = playerService.convertExpToLvl(newPlayer);
        newPlayer.setLevel(level);

        int expForNextLvl = playerService.calculateExpForNextLvl(newPlayer);
        newPlayer.setUntilNextLevel(expForNextLvl);

        if (newPlayer.getBanned() == null) {
            newPlayer.setBanned(false);
        }

        return new ResponseEntity<>(playerService.savePlayer(newPlayer), HttpStatus.OK);
    }

    @PostMapping(path = "/rest/players/{id}")
    public ResponseEntity<Player> updatePlayer(@RequestBody Player updatePlayer, @PathVariable long id) {
        if (id <= 0) {
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }
        if (!playerService.findById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.valueOf(404));
        }

        Player player = playerService.findById(id).get();

        if (updatePlayer.getName() != null && updatePlayer.getName().length() <= 13 && !updatePlayer.getName().isEmpty()) {
            player.setName(updatePlayer.getName());
        }

        if (updatePlayer.getTitle() != null && updatePlayer.getTitle().length() <= 30 && !updatePlayer.getTitle().isEmpty()) {
            player.setTitle(updatePlayer.getTitle());
        }

        if (updatePlayer.getRace() != null) {
            player.setRace(updatePlayer.getRace());
        }

        if (updatePlayer.getProfession() != null) {
            player.setProfession(updatePlayer.getProfession());
        }

        if (updatePlayer.getBirthday() != null) {
            if (updatePlayer.getBirthday().getTime() <= 0) {
                return new ResponseEntity<>(HttpStatus.valueOf(400));
            }
            player.setBirthday(updatePlayer.getBirthday());
        }

        if (updatePlayer.getBanned() != null) {
            player.setBanned(updatePlayer.getBanned());
        }

        if (updatePlayer.getExperience() != null) {
            if (updatePlayer.getExperience() < 0 || updatePlayer.getExperience() > 10000000) {
                return new ResponseEntity<>(HttpStatus.valueOf(400));
            }
            player.setExperience(updatePlayer.getExperience());
            player.setLevel(playerService.convertExpToLvl(player));
            player.setUntilNextLevel(playerService.calculateExpForNextLvl(player));
        }

        return new ResponseEntity<>(playerService.savePlayer(player), HttpStatus.OK);
    }
}