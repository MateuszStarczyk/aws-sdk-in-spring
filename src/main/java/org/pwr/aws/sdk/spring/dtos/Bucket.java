package org.pwr.aws.sdk.spring.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class Bucket {

    @JsonProperty("name")
    @Setter @Getter private String name;
}
