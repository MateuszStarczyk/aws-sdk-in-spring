package org.pwr.aws.sdk.spring.dtos;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Table {
    private String name;
    private Long sizeBytes;
    private Instant creationDate;

    public Table(String name, Long sizeBytes, Instant creationDate) {
        this.name = name;
        this.sizeBytes = sizeBytes;
        this.creationDate = creationDate;
    }

    public String getName() {
        return name;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public String getFormattedCreationDate() {
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        return DATE_TIME_FORMATTER.format(getCreationDate());
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

}
