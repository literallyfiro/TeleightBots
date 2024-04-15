package org.teleight.teleightbots.codegen.generator;

import com.squareup.javapoet.JavaFile;

public interface Generator<T> {

    JavaFile generate(String key, T t);

}
