package org.teleight.teleightbots.codegen.generator.custom;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import javax.lang.model.element.Modifier;

import static org.teleight.teleightbots.codegen.generator.generators.Generator.JAVA_CLASS_CLASSNAME;

public class ChatMemberCustomGenerator implements CustomGenerator {
    @Override
    public void add(TypeSpec.Builder classBuilder, String className) {
        if (className.equals("ChatMember")) {
            classBuilder.addMethod(MethodSpec.methodBuilder("user")
                    .returns(ClassName.get("org.teleight.teleightbots.api.objects", "User"))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .build());
            classBuilder.addMethod(MethodSpec.methodBuilder("type")
                    .returns(ClassName.get("", "ChatMemberType"))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .build());

            ClassName chatMemberOwnerClassName = ClassName.get("org.teleight.teleightbots.api.objects", "ChatMemberOwner");
            ClassName chatMemberAdministratorClassName = ClassName.get("org.teleight.teleightbots.api.objects", "ChatMemberAdministrator");

            classBuilder.addMethod(MethodSpec.methodBuilder("isAdmin")
                    .returns(boolean.class)
                    .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
                    .addStatement("return this instanceof $T || this instanceof $T", chatMemberOwnerClassName, chatMemberAdministratorClassName)
                    .build());

            classBuilder.addType(generateChatMemberType());
        } else {

            String correctMemberType = switch (className) {
                case "ChatMemberOwner" -> "CREATOR";
                case "ChatMemberAdministrator" -> "ADMINISTRATOR";
                case "ChatMemberMember" -> "MEMBER";
                case "ChatMemberRestricted" -> "RESTRICTED";
                case "ChatMemberLeft" -> "LEFT";
                case "ChatMemberBanned" -> "BANNED";
                default -> throw new IllegalStateException("Unexpected value: " + className);
            };
            classBuilder.addMethod(MethodSpec.methodBuilder("type")
                    .returns(ClassName.get("", "ChatMemberType"))
                    .addAnnotation(Override.class)
                    .addStatement("return ChatMemberType.$L", correctMemberType)
                    .addModifiers(Modifier.PUBLIC)
                    .build());
        }
    }

    private TypeSpec generateChatMemberType() {
        ClassName chatMemberClassName = ClassName.get("org.teleight.teleightbots.api.objects", "ChatMember");
        TypeName classExtendsChatMember = WildcardTypeName.subtypeOf(chatMemberClassName);
        TypeName classParameter = ParameterizedTypeName.get(JAVA_CLASS_CLASSNAME, classExtendsChatMember);

        return TypeSpec.enumBuilder("ChatMemberType")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addEnumConstant("CREATOR", TypeSpec.anonymousClassBuilder("$S, ChatMemberOwner.class", "creator").build())
                .addEnumConstant("ADMINISTRATOR", TypeSpec.anonymousClassBuilder("$S, ChatMemberAdministrator.class", "administrator").build())
                .addEnumConstant("MEMBER", TypeSpec.anonymousClassBuilder("$S, ChatMemberMember.class", "member").build())
                .addEnumConstant("RESTRICTED", TypeSpec.anonymousClassBuilder("$S, ChatMemberRestricted.class", "restricted").build())
                .addEnumConstant("LEFT", TypeSpec.anonymousClassBuilder("$S, ChatMemberLeft.class", "left").build())
                .addEnumConstant("BANNED", TypeSpec.anonymousClassBuilder("$S, ChatMemberBanned.class", "kicked").build())
                .addField(String.class, "fieldValue", Modifier.PRIVATE, Modifier.FINAL)
                .addField(classParameter, "wrapperClass", Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(String.class, "fieldValue")
                        .addParameter(classParameter, "wrapperClass")
                        .addStatement("this.$N = $N", "fieldValue", "fieldValue")
                        .addStatement("this.$N = $N", "wrapperClass", "wrapperClass")
                        .build())
                .addMethod(MethodSpec.methodBuilder("getFieldValue")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(String.class)
                        .addStatement("return this.$N", "fieldValue")
                        .build())
                .addMethod(MethodSpec.methodBuilder("getWrapperClass")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(classParameter)
                        .addStatement("return this.$N", "wrapperClass")
                        .build())
                .build();


    }
}
