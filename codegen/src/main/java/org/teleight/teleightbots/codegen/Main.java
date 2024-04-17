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

@SuppressWarnings("SameParameterValue")
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        new CodeGenerator().generateApiClasses(getLatestApiJsonFromGithub("Teleight", "telegram-bot-api-spec"));
    }

    private static Reader getLatestApiJsonFromGithub(String owner, String repo) throws IOException, InterruptedException {
        try (var httpClient = HttpClient.newHttpClient()) {
            // Get the latest release URL
            URI latestReleaseUrl = URI.create("https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest");

            // Build the GET request for latest release
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
