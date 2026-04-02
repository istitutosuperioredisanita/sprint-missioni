package it.cnr.si.missioni.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;

public class BaseSpecification {

    public static <T> Specification<T> eq(String field, Object value) {
        if (value == null) return null;
        return (root, query, cb) -> cb.equal(root.get(field), value);
    }

    public static <T> Specification<T> like(String field, String value) {
        if (value == null || value.isBlank()) return null;
        return (root, query, cb) ->
                cb.like(cb.upper(root.get(field)), "%" + value.toUpperCase() + "%");
    }

    public static <T> Specification<T> ge(String field, Number value) {
        if (value == null) return null;
        return (root, query, cb) -> cb.ge(root.get(field), value);
    }

    public static <T> Specification<T> le(String field, Number value) {
        if (value == null) return null;
        return (root, query, cb) -> cb.le(root.get(field), value);
    }

    public static <T> Specification<T> in(String field, Collection<?> values) {
        if (values == null || values.isEmpty()) return null;
        return (root, query, cb) -> root.get(field).in(values);
    }

    public static <T> Specification<T> dataInserimentoDa(String value) {
        if (value == null) return null;

        LocalDate date = LocalDate.parse(value);

        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("dataInserimento"),
                        date.atStartOfDay(ZoneId.systemDefault())
                );
    }

    public static <T> Specification<T> dataInserimentoA(String value) {
        if (value == null) return null;

        LocalDate date = LocalDate.parse(value);

        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get("dataInserimento"),
                        date.plusDays(1).atStartOfDay(ZoneId.systemDefault())
                );
    }

    public static <T> Specification<T> dataMissioneDa(String value) {

        if (value == null) return null;

        LocalDate date = LocalDate.parse(value);

        return (root, query, cb) -> cb.or(
                cb.greaterThanOrEqualTo(
                        root.get("dataInizioMissione"),
                        date.atStartOfDay(ZoneId.systemDefault())
                ),
                cb.greaterThanOrEqualTo(
                        root.get("dataFineMissione"),
                        date.atStartOfDay(ZoneId.systemDefault())
                )
        );
    }

    public static <T> Specification<T> dataMissioneA(String value) {

        if (value == null) return null;

        LocalDate date = LocalDate.parse(value);

        return (root, query, cb) -> cb.or(
                cb.lessThan(
                        root.get("dataInizioMissione"),
                        date.plusDays(1).atStartOfDay(ZoneId.systemDefault())
                ),
                cb.lessThan(
                        root.get("dataFineMissione"),
                        date.plusDays(1).atStartOfDay(ZoneId.systemDefault())
                )
        );
    }
}