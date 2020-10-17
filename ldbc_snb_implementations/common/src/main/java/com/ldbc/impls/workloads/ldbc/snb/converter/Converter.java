package com.ldbc.impls.workloads.ldbc.snb.converter;

import com.ldbc.driver.workloads.ldbc.snb.interactive.LdbcUpdate1AddPerson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class Converter {

    final static String DATAGEN_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'+0000'";

    /**
     * Converts epoch seconds to a date to the format of the converter (e.g. PostgreSQL-style timestamps).
     *
     * @param timestamp
     * @return
     */
    public String convertDateTime(long timestamp) {
        return convertDateTime(new Date(timestamp));
    }

    public String convertDateTime(Date date) {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATAGEN_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return "'" + sdf.format(date) + "'";
    }

    public String convertDate(long timestamp) {
        return convertDateTime(new Date(timestamp));
    }

    public String convertDate(Date date) {
        return convertDateTime(date);
    }

    /**
     * Converts timestamp strings (in the format produced by DATAGEN) ({@value #DATAGEN_FORMAT})
     * to a date.
     *
     * @param timestamp
     * @return
     */
    public long convertTimestampToEpoch(String timestamp) throws ParseException {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATAGEN_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.parse(timestamp).toInstant().toEpochMilli();
    }

    /**
     * Surrounds a string in single quotes and escape single quotes in the string itself.
     *
     * @param value
     * @return
     */
    public String convertString(String value) {
        return "'" + value.replace("'", "\\'") + "'";
    }

    public String convertInteger(int value) {
        return Integer.toString(value);
    }

    /**
     * Convert strings to a comma-separated list between square brackets.
     *
     * @param values
     * @return
     */
    public String convertStringList(List<String> values) {
        String res = "[";
        res += values
                .stream()
                .map(v -> "'" + v + "'")
                .collect(Collectors.joining(", "));
        res += "]";
        return res;
    }

    /**
     * Convert a list of longs to a comma-separated list between square brackets.
     *
     * @param values
     * @return
     */
    public String convertLongList(List<Long> values) {
        String res = "[";
        res += values
                .stream()
                .map(v -> v.toString())
                .collect(Collectors.joining(", "));
        res += "]";
        return res;
    }

    /**
     * Convert a list of longs to a comma-separated list between square brackets.
     *
     * @param values
     * @return
     */
    public String convertOrganisations(List<LdbcUpdate1AddPerson.Organization> values) {
        String res = "[";
        res += values
                .stream()
                .map(v -> v.toString())
                .collect(Collectors.joining(", "));
        res += "]";
        return res;
    }

    public String convertBlacklist(List<String> words) {
        return convertStringList(words);
    }

    /**
     * Some implementations, e.g. the SPARQL one, will not work with a simple toString and require some tinkering,
     * e.g. padding the id with '0' characters.
     *
     * @param value
     * @return
     */
    public String convertId(long value) {
        return Long.toString(value);
    }

    /**
     * Some implementation, e.g. the SPARQL one, require a different id for updates:
     * while SparqlConverter#convertId() wraps the value with `"00000..."^^xsd:long`,
     * updates require plain `00000...` format.
     *
     * @param value
     * @return
     */
    public String convertIdForInsertion(long value) {
        return convertId(value);
    }

}
