package io.github.hello09x.fakeplayer.util.update;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Release {

    @SerializedName("tag_name")
    private String tagName;

    private String body;

}
