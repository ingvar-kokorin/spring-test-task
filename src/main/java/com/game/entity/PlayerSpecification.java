package com.game.entity;

import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class PlayerSpecification {
    public static Specification<Player> getPlayerByFilter(final PlayerRequestCriteria criteria) {
        Specification<Player> specification;
        Specification<Player> temp = null;

        if (criteria.getName() != null) {
            specification = getPlayerByName(criteria.getName());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        if (criteria.getTitle() != null) {
            specification = getPlayerByTitle(criteria.getTitle());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        if (criteria.getRace() != null) {
            specification = getPlayerByRace(criteria.getRace());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        if (criteria.getRace() != null) {
            specification = getPlayerByProfession(criteria.getProfession());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        // THIS ONE
        if (criteria.getAfter() != null) {
            specification = getPlayerByAfter(criteria.getAfter());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        return null;
    }
    // THIS ONE
    private static Specification<Player> getPlayerByAfter(final Long after) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("birthday")), new Date(after)));
    }

    private static Specification<Player> getPlayerByName(final String name) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name + "%"));
    }

    private static Specification<Player> getPlayerByTitle(String title) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title + "%"));
    }

    private static Specification<Player> getPlayerByRace(Race race) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.lower(root.get("race")), String.valueOf(race)));
    }

    private static Specification<Player> getPlayerByProfession(Profession profession) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("profession")), String.valueOf(profession)));
    }


}

