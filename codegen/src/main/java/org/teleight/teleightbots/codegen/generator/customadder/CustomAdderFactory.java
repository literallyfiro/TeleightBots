package org.teleight.teleightbots.codegen.generator.customadder;

import org.teleight.teleightbots.codegen.generator.customadder.impl.ChatMemberCustomAdder;

public class CustomAdderFactory {

    public static CustomAdder getCustomAdder(String className) {
        return switch (className) {
            case "ChatMember",
                 "ChatMemberOwner",
                 "ChatMemberAdministrator",
                 "ChatMemberMember",
                 "ChatMemberRestricted",
                 "ChatMemberLeft",
                 "ChatMemberBanned" -> new ChatMemberCustomAdder();
            case "User" -> null;
            default -> null;
        };
    }

}
