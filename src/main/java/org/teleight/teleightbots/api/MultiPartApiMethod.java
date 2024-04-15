package org.teleight.teleightbots.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.teleight.teleightbots.utils.MultiPartBodyPublisher;

import java.io.Serializable;

public interface MultiPartApiMethod<R extends Serializable> extends ApiMethod<R> {

    void buildRequest(MultiPartBodyPublisher bodyCreator) throws JsonProcessingException;

}
