package com.game.entity;

import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;

public class PlayerSpecification {
    public static Specification<Player> getPlayerByFilter(final PlayerRequestCriteria criteria) {
        Specification<Player> specification = null;
        Specification<Player> temp = null;

        if (criteria.getName() != null) {
            specification = getPlayerByName(criteria.getName());
            temp = specification;
        }

        if (criteria.getTitle() != null) {
            specification = getPlayerByTitle(criteria.getTitle());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        if (criteria.getRace() != null) {
            specification = getPlayerByRace(criteria.getRace());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        if (criteria.getProfession() != null) {
            specification = getPlayerByProfession(criteria.getProfession());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        if (criteria.getAfter() != null) {
            specification = getPlayerByAfter(criteria.getAfter());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        if (criteria.getBefore() != null) {
            specification = getPlayerByBefore(criteria.getBefore());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        if (criteria.getBefore() != null) {
            specification = getPlayerByBanned(criteria.getBanned());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        if (criteria.getMinExperience() != null) {
            specification = getPlayerByMinExperience(criteria.getMinExperience());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        if (criteria.getMaxExperience() != null) {
            specification = getPlayerByMaxExperience(criteria.getMaxExperience());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        if (criteria.getMinLevel() != null) {
            specification = getPlayerByMinLevel(criteria.getMinLevel());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        if (criteria.getMaxLevel() != null) {
            specification = getPlayerByMaxLevel(criteria.getMaxLevel());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        return temp;
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
                criteriaBuilder.equal(criteriaBuilder.lower(root.get("race")), race));
    }

    private static Specification<Player> getPlayerByProfession(Profession profession) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.lower(root.get("profession")), profession));
    }

    private static Specification<Player> getPlayerByAfter(final Long after) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), new Date(after)));
    }

    private static Specification<Player> getPlayerByBefore(final Long before) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), new Date(before)));
    }

    private static Specification<Player> getPlayerByBanned(Boolean banned) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("banned"), banned));
    }

    private static Specification<Player> getPlayerByMinExperience(Integer minExperience) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), minExperience));
    }

    private static Specification<Player> getPlayerByMaxExperience(Integer maxExperience) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("experience"), maxExperience));
    }

    private static Specification<Player> getPlayerByMinLevel(Integer minLevel) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("level"), minLevel));
    }

    private static Specification<Player> getPlayerByMaxLevel(Integer maxLevel) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("level"), maxLevel));
    }

}

