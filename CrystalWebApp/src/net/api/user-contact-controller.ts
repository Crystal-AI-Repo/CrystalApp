import type {PagedData} from "@/net/PagedData.ts";
import {applicationFormUrlEncodedHeader, doGet, doPost} from "@/net/axios-request.ts";
import {userTokenHeader} from "@/utils/auth-utils.ts";
import type {ChatCharacter} from "@/net/api/chat-character-controller.ts";

export interface UserContact {
    id: number,
    contactType: number,
    chatTargetId: number
}

export interface UserContactVO {
    contact: UserContact,
    reifiedContact: ChatCharacter | null
}

export async function getUserContactList(page: number) {
    return (await doGet<PagedData<UserContactVO>>("/api/contact/list", {...userTokenHeader()}, { page: page }))
}

export async function addChatCharacterContact(characterId: number) {
    return (await doPost("/api/contact/addCharacterChat", {...userTokenHeader(), ...applicationFormUrlEncodedHeader}, { characterId: characterId }))
}

export async function sendMessage(contactId: number, messageId: number, message: string) {
    return (await doPost(
        "/api/contact/sendMessage",
        {...userTokenHeader(), ...applicationFormUrlEncodedHeader},
        { contactId: contactId, messageId: messageId, message: message }
    ))
}