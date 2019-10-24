package edu.stanford.bmir.protege.web.server.match;

import com.google.gwt.i18n.shared.DateTimeFormat;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.ZonedDateTime.parse;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 15 Jun 2018
 */
public class DateIsBeforeMatcher implements Matcher<String> {

    @Nonnull
    private final LocalDate localDate;

    public DateIsBeforeMatcher(@Nonnull LocalDate date) {
        this.localDate = checkNotNull(date);
    }

    @Override
    public boolean matches(@Nonnull String value) {
        if(!OWL2Datatype.XSD_DATE_TIME.getPattern().matcher(value).matches()) {
            return false;
        }
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(value);
            LocalDate dateValue = zonedDateTime.toLocalDate();
            return dateValue.isBefore(localDate);
        } catch (Exception e) {
            return false;
        }
    }
}
