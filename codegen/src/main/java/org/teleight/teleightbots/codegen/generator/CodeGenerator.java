package org.teleight.teleightbots.codegen.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.teleight.teleightbots.codegen.generator.custom.CustomGenerator;
import org.teleight.teleightbots.codegen.generator.custom.CustomGeneratorFactory;
import org.teleight.teleightbots.codegen.generator.generators.Generator;
import org.teleight.teleightbots.codegen.generator.generators.MethodGenerator;
import org.teleight.teleightbots.codegen.generator.generators.ObjectGenerator;
import org.teleight.teleightbots.codegen.json.ApiRoot;
import org.teleight.teleightbots.codegen.json.deserializer.ClassNameDeserializer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class CodeGenerator {

    public static final File autogeneratedPath = new File("src/autogenerated/java");

    public void generateApiClasses(Reader reader) {
        final Gson gson = new GsonBuilder().registerTypeAdapter(TypeName[].class, new ClassNameDeserializer()).create();
        final ApiRoot root = gson.fromJson(reader, ApiRoot.class);

        generateClasses(root.types(), new ObjectGenerator());
        generateClasses(root.methods(), new MethodGenerator());
    }

    private <T> void generateClasses(Map<String, T> items, Generator<? super T> generator) {
        System.out.println("Generating " + items.size() + " classes with generator " + generator.getClass().getSimpleName());
        try {
            for (Map.Entry<String, T> entry : items.entrySet()) {

                System.out.println("Generating " + entry.getKey() + " class");

                final String name = entry.getKey();
                final T item = entry.getValue();

                final TypeSpec.Builder typeSpecBuilder = generator.generate(name, item);

                final String className = typeSpecBuilder.build().name;
                final CustomGenerator customGenerator = CustomGeneratorFactory.getCustomAdder(className);
                if (customGenerator != null)
                    customGenerator.generate(typeSpecBuilder, className);

                final JavaFile javaFile = JavaFile.builder(generator.getPackageName(), typeSpecBuilder.build())
                        .skipJavaLangImports(true)
                        .indent("    ")
                        .build();

                javaFile.writeTo(autogeneratedPath);
            }
        } catch (IOException e) {
            throw new GenerationException("Failed to generate classes", e);
        }
    }

}