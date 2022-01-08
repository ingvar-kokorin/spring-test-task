package com.game.entity;

import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;

public class PlayerSpecification {
    public static Specification<Player> getPlayerByFilter(final PlayerRequestCriteria criteria) {
        Specification<Player> specification = null;
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

        if (criteria.getProfession() != null) {
            specification = getPlayerByProfession(criteria.getProfession());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        if (criteria.getAfter() != null) {
            specification = getPlayerByAfter(criteria.getAfter());
            temp = temp == null ? specification : Specification.where(specification).and(temp);
        }

        return temp;
    }

    private static Specification<Player> getPlayerByAfter(final Long after) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(criteriaBuilder.lower(root.get("birthday")), new Date(after)));
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
}

