package org.teleight.teleightbots.codegen;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.teleight.teleightbots.codegen.generator.CodeGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

@SuppressWarnings("SameParameterValue")
public class GeneratorMain {

    private static final String REPO_OWNER = "Teleight";
    private static final String REPO_NAME = "telegram-bot-api-spec";

    public static void main(String[] args) throws IOException, InterruptedException {
        final boolean useGithub = System.getenv("github") != null;
        final CodeGenerator codeGenerator = new CodeGenerator();

        if (useGithub) {
            codeGenerator.generateApiClasses(getLatestApiJsonFromGithub());
        } else {
            try (final InputStream in = GeneratorMain.class.getResourceAsStream("/api.json")) {
                Reader reader = new InputStreamReader(Objects.requireNonNull(in));
                codeGenerator.generateApiClasses(reader);
            }
        }
    }

    private static Reader getLatestApiJsonFromGithub() throws IOException, InterruptedException {
        try (var httpClient = HttpClient.newHttpClient()) {
            // Get the latest release URL
            URI latestReleaseUrl = URI.create("https://api.github.com/repos/" + REPO_OWNER + "/" + REPO_NAME + "/releases/latest");

            // Build the GET request for the latest release
            HttpRequest latestReleaseRequest = HttpRequest.newBuilder()
                    .uri(latestReleaseUrl)
                    .setHeader("Accept", "application/vnd.github.v3+json")
                    .build();

            // Send request and parse response for latest release
            HttpResponse<String> releaseResponse = httpClient.send(latestReleaseRequest, HttpResponse.BodyHandlers.ofString());

            // Check for successful response
            if (releaseResponse.statusCode() != 200) {
                throw new IOException("Failed to get latest release: " + releaseResponse.statusCode());
            }

            // Parse JSON to get asset URL
            String releaseJson = releaseResponse.body();
            JsonElement releaseObject = JsonParser.parseString(releaseJson);
            String assetsUrl = releaseObject.getAsJsonObject().get("assets_url").getAsString();

            // Build asset URL for api.json
            URI assetUrl = URI.create(assetsUrl + "/api.json");

            // Build the GET request for downloading api.json
            HttpRequest downloadRequest = HttpRequest.newBuilder()
                    .uri(assetUrl)
                    .build();

            // Send request and handle response for downloading api.json
            HttpResponse<InputStream> downloadResponse = httpClient.send(downloadRequest, HttpResponse.BodyHandlers.ofInputStream());

            if (downloadResponse.statusCode() != 200) {
                throw new IOException("Failed to download api.json: " + downloadResponse.statusCode());
            }

            return new InputStreamReader(downloadResponse.body());
        }
    }

}
