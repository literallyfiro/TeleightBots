package org.teleight.teleightbots.api.objects.chat.member;

import org.teleight.teleightbots.api.objects.User;

import java.io.Serializable;

public sealed interface ChatMember extends Serializable permits
        ChatMemberOwner,
        ChatMemberAdministrator,
        ChatMemberMember,
        ChatMemberRestricted,
        ChatMemberLeft,
        ChatMemberBanned {

    ChatMemberType type();

    User user();

    default boolean isAdmin(){
        return this instanceof ChatMemberOwner || this instanceof ChatMemberAdministrator;
    }

}
