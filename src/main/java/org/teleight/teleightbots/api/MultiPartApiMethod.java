package org.teleight.teleightbots.api;

import org.jetbrains.annotations.NotNull;
import org.teleight.teleightbots.api.objects.InputFile;

import java.io.Serializable;
import java.util.Map;

/**
 * Base API method that supports multipart requests, allowing both parameters and files to be sent.
 *
 * @param <R> The type of the response object expected from this API method.
 */
public interface MultiPartApiMethod<R extends Serializable> extends ApiMethod<R> {

    /**
     * Retrieves the parameters associated with this API method.
     *
     * @return A {@code Map} containing the parameters as key-value pairs.
     */
    @NotNull
    Map<String, Object> getParams();

    /**
     * Retrieves the files associated with this API method.
     *
     * @return A {@code Map} containing the files as key-value pairs, where the key is the parameter name and the value is the file.
     */
    @NotNull
    Map<String, InputFile> getFiles();

}
