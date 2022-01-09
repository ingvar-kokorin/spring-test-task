package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.PlayerRequestCriteria;
import com.game.entity.PlayerSpecification;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Optional<Player> findById(Long id) {
        return playerRepository.findById(id);
    }

    public List<Player> findAll(PlayerRequestCriteria criteria) {
        String playerOrder = criteria.getOrder() != null ? criteria.getOrder().getFieldName() : PlayerOrder.ID.getFieldName();
        int number = criteria.getPageNumber() != null ? criteria.getPageNumber() : 0;
        int size = criteria.getPageSize() != null ? criteria.getPageSize() : 3;

        Page<Player> page = playerRepository.findAll(PlayerSpecification.getPlayerByFilter(criteria), PageRequest.of(number,
                size, Sort.by(playerOrder)));

        return page.getContent();
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

    public List<Player> count(
            String name,
            String title,
            Race race,
            Profession profession,
            Long after,
            Long before,
            Boolean banned,
            Integer minExperience,
            Integer maxExperience,
            Integer minLevel,
            Integer maxLevel
    ) {
        List<Player> players = playerRepository.findAll();

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

        return players;
    }

}
