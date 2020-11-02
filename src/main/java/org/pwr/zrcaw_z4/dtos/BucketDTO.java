package org.pwr.zrcaw_z4.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class BucketDTO {

    @JsonProperty("name")
    @Setter @Getter private String name;
}
