package org.bouncycastle.its;

import java.util.Date;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Duration;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT16;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.ValidityPeriod;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/ITSValidityPeriod.class */
public class ITSValidityPeriod {
    private final long startDate;
    private final UINT16 duration;
    private final Unit timeUnit;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/ITSValidityPeriod$Builder.class */
    public static class Builder {
        private final long startDate;

        Builder(Date date) {
            this.startDate = date.getTime();
        }

        public ITSValidityPeriod plusYears(int i) {
            return new ITSValidityPeriod(this.startDate, UINT16.valueOf(i), Unit.years);
        }

        public ITSValidityPeriod plusSixtyHours(int i) {
            return new ITSValidityPeriod(this.startDate, UINT16.valueOf(i), Unit.sixtyHours);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/ITSValidityPeriod$Unit.class */
    public enum Unit {
        microseconds(0),
        milliseconds(1),
        seconds(2),
        minutes(3),
        hours(4),
        sixtyHours(5),
        years(6);

        private final int unitTag;

        Unit(int i) {
            this.unitTag = i;
        }
    }

    public static Builder from(Date date) {
        return new Builder(date);
    }

    public ITSValidityPeriod(ValidityPeriod validityPeriod) {
        this.startDate = validityPeriod.getStart().getValue().longValue();
        Duration duration = validityPeriod.getDuration();
        this.duration = duration.getDuration();
        this.timeUnit = Unit.values()[duration.getChoice()];
    }

    ITSValidityPeriod(long j, UINT16 uint16, Unit unit) {
        this.startDate = j;
        this.duration = uint16;
        this.timeUnit = unit;
    }

    public Date getStartDate() {
        return new Date(this.startDate);
    }

    public ValidityPeriod toASN1Structure() {
        return ValidityPeriod.builder().setStart(new Time32(this.startDate / 1000)).setDuration(new Duration(this.timeUnit.unitTag, this.duration)).createValidityPeriod();
    }
}
