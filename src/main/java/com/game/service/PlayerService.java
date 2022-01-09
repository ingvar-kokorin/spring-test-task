package com.game.service;

import com.game.entity.Player;
import com.game.entity.PlayerRequestCriteria;
import com.game.entity.Profession;
import com.game.entity.Race;

import java.util.List;
import java.util.Optional;

public interface PlayerService {

    Optional<Player> findById(Long id);

    void deleteById(Long id);

    Long getCount();

    Player savePlayer(Player player);

    Integer convertExpToLvl(Player player);

    Integer calculateExpForNextLvl(Player player);

    boolean isRequestContainsNull(Player player);

    boolean isRequestMatchCriteria(Player player);

    List<Player> count(
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
    );

    List<Player> findAll(PlayerRequestCriteria criteria);
}
