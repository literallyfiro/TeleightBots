package org.teleight.teleightbots.codegen.generator.custom;

public class CustomGeneratorFactory {

    public static CustomGenerator getCustomAdder(String className) {
        return switch (className) {
            case "ChatMember",
                 "ChatMemberOwner",
                 "ChatMemberAdministrator",
                 "ChatMemberMember",
                 "ChatMemberRestricted",
                 "ChatMemberLeft",
                 "ChatMemberBanned" -> new ChatMemberCustomGenerator();
            case "User" -> null;
            default -> null;
        };
    }

}
