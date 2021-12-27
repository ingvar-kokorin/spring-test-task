package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Optional<Player> findById(Long id) {
        return playerRepository.findById(id);
    }

    public Iterable<Player> findAll() {
        return playerRepository.findAll();
    }

    public void deleteById(Long id) {
        playerRepository.deleteById(id);
    }

    public Long getCount() {
        return playerRepository.count();
    }

    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    public Player updatePlayer(Player player) {
        return playerRepository.save(player);
    }

    public Integer convertExpToLvl(Player player) {
        return (int) ((Math.sqrt(2500 + (200 * player.getExperience())) - 50) / 100);
    }

    public Integer calculateExpForNextLvl(Player player) {
        int lvl = player.getLevel();
        int exp = player.getExperience();

        return 50 * (lvl + 1) * (lvl + 2) - exp;
    }

    public boolean isRequestContainsNull(Player player) {
        return player.getName() == null || player.getTitle() == null || player.getRace() == null
                || player.getProfession() == null || player.getBirthday() == null
                || player.getExperience() == null;
    }

    public boolean isRequestMatchCriteria(Player player) {
        return player.getName().length() <= 12 && player.getTitle().length() <= 30 && !player.getName().isEmpty() &&
                player.getBirthday().getTime() >= 0 && player.getExperience() <= 10000000;
    }
}
