package org.teleight.teleightbots.codegen.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;

public sealed interface Generator<T> permits ObjectGenerator, MethodGenerator {

    ClassName apiMethodInterface = ClassName.get("org.teleight.teleightbots.api", "ApiMethod");
    ClassName apiResultInterface = ClassName.get("org.teleight.teleightbots.api", "ApiResult");
    ClassName apiResultSerializableInterface = ClassName.get("org.teleight.teleightbots.api", "ApiMethodSerializable");
    ClassName notNullAnnotation = ClassName.get("org.jetbrains.annotations", "NotNull");
    String methodsPackageName = "org.teleight.teleightbots.api.methods";
    String objectsPackageName = "org.teleight.teleightbots.api.objects";

    JavaFile generate(String key, T t);

    default String getCorrectPackageName(String type) {
        if (!CodeGenerator.isTelegramPrimitive(type)) {
            return objectsPackageName;
        }
        return methodsPackageName;
    }

}
