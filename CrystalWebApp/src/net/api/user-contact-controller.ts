import type {PagedData} from "@/net/PagedData.ts";
import {
    applicationFormUrlEncoded,
    applicationFormUrlEncodedHeader,
    applicationJson,
    doGet,
    doPost
} from "@/net/axios-request.ts";
import {getUserToken, userTokenHeader} from "@/utils/auth-utils.ts";
import type {ChatCharacter} from "@/net/api/chat-character-controller.ts";
import {AxiosOpenApiRequest} from "@/net/axios-open-api.ts";

export interface UserContact {
    id: string,
    contactType: number,
    chatTargetId: string
}

export interface UserContactVO {
    contact: UserContact,
    reifiedContact: ChatCharacter | null
}

export async function getUserContactList(page: number) {
    return (await doGet<PagedData<UserContactVO>>("/api/contact/list", {...userTokenHeader()}, { page: page }))
}

export async function addChatCharacterContact(characterId: string) {
    return (await doPost("/api/contact/addCharacterChat", {...userTokenHeader(), ...applicationFormUrlEncodedHeader}, { characterId: characterId }))
}

const axiosOpenApiRequest = new AxiosOpenApiRequest<{contactId: string, messageId: string, message: string}>("/api/contact/sendMessage", getUserToken())

export async function *sendMessageToContact(contactId: string, messageId: string, message: string) {
    for await (const pack of axiosOpenApiRequest.sendMessage({ contactId: contactId, messageId: messageId, message: message })) {
        yield pack
    }
}