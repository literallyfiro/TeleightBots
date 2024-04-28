package org.teleight.teleightbots.codegen.generator.custom;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

public non-sealed class ChatCustomGenerator implements CustomGenerator {

    @Override
    public void generate(TypeSpec.Builder classBuilder, String className) {
        classBuilder.addMethod(MethodSpec.methodBuilder("isGroup")
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return Type.GROUP_CHAT_TYPE.valueField.equals(type)")
                .build());
        classBuilder.addMethod(MethodSpec.methodBuilder("isChannel")
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return Type.CHANNEL_CHAT_TYPE.valueField.equals(type)")
                .build());
        classBuilder.addMethod(MethodSpec.methodBuilder("isUser")
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return Type.USER_CHAT_TYPE.valueField.equals(type)")
                .build());
        classBuilder.addMethod(MethodSpec.methodBuilder("isSuperGroup")
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return Type.SUPERGROUP_CHAT_TYPE.valueField.equals(type)")
                .build());

        TypeSpec typeTypeSpec = TypeSpec.enumBuilder("Type")
                .addModifiers(Modifier.PUBLIC)
                .addEnumConstant("USER_CHAT_TYPE", TypeSpec.anonymousClassBuilder("$S", "private").build())
                .addEnumConstant("GROUP_CHAT_TYPE", TypeSpec.anonymousClassBuilder("$S", "group").build())
                .addEnumConstant("CHANNEL_CHAT_TYPE", TypeSpec.anonymousClassBuilder("$S", "channel").build())
                .addEnumConstant("SUPERGROUP_CHAT_TYPE", TypeSpec.anonymousClassBuilder("$S", "supergroup").build())
                .addField(FieldSpec.builder(String.class, "valueField", Modifier.PRIVATE, Modifier.FINAL).build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(String.class, "valueField")
                        .addStatement("this.valueField = valueField")
                        .build())
                .build();

        classBuilder.addType(typeTypeSpec);
    }
}
