package io.github.hello09x.fakeplayer.core.util.update;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Release {

    /**
     * GitHub tag name
     */
    @SerializedName("tag_name")
    private String tagName;

    /**
     * GitHub release content
     */
    private String body;

}
