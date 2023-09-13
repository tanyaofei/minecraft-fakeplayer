package io.github.hello09x.fakeplayer.util.update;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class UpdateChecker {

    private final static Gson gson = new Gson();

    private final String author;

    private final String repository;

    public UpdateChecker(@NotNull String author, @NotNull String repository) {
        this.author = author;
        this.repository = repository;
    }

    public static boolean isNew(@NotNull String current, @NotNull String other) {
        var split1 = current.split("\\.");
        var split2 = other.split("\\.");

        if (split2.length > split1.length) {
            // 如果 other 的版本号位数更多, 则认为是新版本号
            return true;
        }

        if (split2.length < split1.length) {
            // 如果 other 的版本号位数更少, 则认为是旧版本
            return false;
        }

        // split2.length == split1.length
        var length = split1.length;
        for (int i = 0; i < length; i++) {
            var v1 = Integer.parseInt(split1[i]);
            var v2 = Integer.parseInt(split2[i]);
            if (v1 == v2) {
                continue;
            }

            return v1 < v2;
        }

        return false;
    }

    public @NotNull Release getLastRelease() throws IOException, InterruptedException {
        var url = String.format("https://api.github.com/repos/%s/%s/releases/latest", this.author, this.repository);
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Not 200 response: " + response.statusCode() + ": " + response.body());
        }

        var body = response.body();
        return gson.fromJson(body, Release.class);
    }


}
