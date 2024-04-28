package org.teleight.teleightbots.codegen.generator.custom;

import com.squareup.javapoet.TypeSpec;

public sealed interface CustomGenerator permits
        ChatCustomGenerator,
        ChatMemberCustomGenerator,
        InputFileCustomGenerator {

    void generate(TypeSpec.Builder classBuilder, String className);

}
