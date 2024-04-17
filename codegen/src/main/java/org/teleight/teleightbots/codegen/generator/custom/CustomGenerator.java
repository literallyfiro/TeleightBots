package org.teleight.teleightbots.codegen.generator.custom;

import com.squareup.javapoet.TypeSpec;

public interface CustomGenerator {

    void add(TypeSpec.Builder classBuilder, String className);

}
