import {applicationFormUrlEncoded, applicationFormUrlEncodedHeader, doGet, doPost} from "@/net/axios-request.ts";
import {userTokenHeader} from "@/utils/auth-utils.ts";
import type {PagedData} from "@/net/PagedData.ts";

export interface ChatCharacter {
    id: number,
    authorUid: number,
    name: string,
    description: string,
    prompt: string,
    greetingMessage: string,
    modelId: string,
    maxContextLength: number,
    avatar: string,
    createdTime: number,
    modifiedTime: number
}

export interface SaveChatCharacterDTO {
    id?: number,
    name: string,
    description: string,
    model: string,
    prompt: string,
    greeting: string
}

export async function getChatCharacterDetails(characterId: number) {
    return (await doGet<ChatCharacter>("/api/character/details", {...userTokenHeader()}, { id: characterId })).data
}

export async function getRecentChatCharacters(page: number) {
    return (await doGet<PagedData<ChatCharacter>>("/api/character/recentCharacters", {}, { page: page })).data
}

export async function getMyCreatedChatCharacters(page: number) {
    return (await doGet<PagedData<ChatCharacter>>("/api/character/myCharacters", {...userTokenHeader()}, { page: page })).data
}

export async function saveChatCharacter(dto: SaveChatCharacterDTO) {
    return await doPost(
        "/api/character/save",
        {...userTokenHeader(), ...applicationFormUrlEncodedHeader},
        {...dto}
    )
}