package com.game.controller;

import com.game.entity.Player;
import com.game.entity.PlayerRequestCriteria;
import com.game.service.PlayerService;
import com.game.service.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public PlayerController(PlayerServiceImpl playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/rest/players")
    public ResponseEntity<List<Player>> findAll(PlayerRequestCriteria criteria) {
        List<Player> players = playerService.filter(criteria.getName(), criteria.getTitle(), criteria.getRace(), criteria.getProfession(),
                criteria.getAfter(), criteria.getBefore(), criteria.getBanned(),
                criteria.getMinExperience(), criteria.getMaxExperience(), criteria.getMinLevel(), criteria.getMaxLevel(),
                criteria.getOrder(), criteria.getPageNumber(), criteria.getPageSize());

        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    @GetMapping("/rest/players/count")
    public ResponseEntity<Long> playerCount(PlayerRequestCriteria criteria) {
        List<Player> players = playerService.count(criteria.getName(), criteria.getTitle(), criteria.getRace(), criteria.getProfession(),
                criteria.getAfter(), criteria.getBefore(), criteria.getBanned(),
                criteria.getMinExperience(), criteria.getMaxExperience(), criteria.getMinLevel(), criteria.getMaxLevel());

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