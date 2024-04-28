package org.teleight.teleightbots.codegen.generator.custom;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.teleight.teleightbots.codegen.generator.generators.Generator.JSON_PROPERTY_CLASSNAME;

public non-sealed class InputFileCustomGenerator implements CustomGenerator {

    @Override
    public void generate(TypeSpec.Builder classBuilder, String className) {
        ClassName inputFileClassName = ClassName.get("org.teleight.teleightbots.api.objects", "InputFile");

        classBuilder.addField(FieldSpec.builder(File.class, "file").addAnnotation(JSON_PROPERTY_CLASSNAME).build());
        classBuilder.addField(FieldSpec.builder(String.class, "fileName").addAnnotation(JSON_PROPERTY_CLASSNAME).build());

        classBuilder.addMethod(MethodSpec.methodBuilder("fromFile")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(File.class, "file")
                .returns(inputFileClassName)
                .addStatement("return new InputFile($L, $L.getName())", "file", "file")
                .build());

        classBuilder.addMethod(MethodSpec.methodBuilder("fromFile")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(String.class, "filePath")
                .returns(inputFileClassName)
                .addStatement("return fromFile(new $T($L))", File.class, "filePath")
                .build());

        classBuilder.addMethod(MethodSpec.methodBuilder("fromResource")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(String.class, "resource")
                .returns(inputFileClassName)
                .beginControlFlow("try")
                .addStatement("$T tempFile = $T.createTempFile($S, $S)", File.class, File.class, "resource_", ".tmp")
                .addStatement("$T resourceStream = $T.class.getClassLoader().getResourceAsStream(resource)", InputStream.class, inputFileClassName)
                .beginControlFlow("if (resourceStream == null)")
                .addStatement("throw new $T($S)", FileNotFoundException.class, "Resource not found")
                .endControlFlow()
                .addStatement("$T.copy(resourceStream, tempFile.toPath(), $T.REPLACE_EXISTING)", Files.class, StandardCopyOption.class)
                .addStatement("resourceStream.close()")
                .addStatement("return new InputFile(tempFile, tempFile.getName())")
                .nextControlFlow("catch ($T e)", IOException.class)
                .addStatement("throw new $T(e)", RuntimeException.class)
                .endControlFlow()
                .build());
    }

}
