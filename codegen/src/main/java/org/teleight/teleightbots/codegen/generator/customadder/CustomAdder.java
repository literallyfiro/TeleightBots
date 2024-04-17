package org.teleight.teleightbots.codegen.generator.customadder;

import com.squareup.javapoet.TypeSpec;

public interface CustomAdder {

    void add(TypeSpec.Builder classBuilder, String className);

}
