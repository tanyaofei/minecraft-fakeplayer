package io.github.hello09x.fakeplayer.core.util.update;

import com.google.gson.Gson;
import io.github.hello09x.devtools.core.version.InvalidVersionException;
import io.github.hello09x.devtools.core.version.Version;
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

    public static boolean isNew(@NotNull String local, @NotNull String remote) {
        Version a, b;
        try {
            a = Version.parse(local);
            b = Version.parse(remote);
        } catch (InvalidVersionException e) {
            return false;
        }

        return a.compareTo(b) < 0;
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
