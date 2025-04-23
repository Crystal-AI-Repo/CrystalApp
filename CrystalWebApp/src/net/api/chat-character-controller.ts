import {applicationFormUrlEncodedHeader, doGet, doPost} from "@/net/axios-request.ts";
import {userTokenHeader} from "@/utils/auth-utils.ts";
import type {PagedData} from "@/net/PagedData.ts";

export interface ChatCharacter {
    id: string,
    authorUid: number,
    name: string,
    description: string,
    prompt: string,
    greetingMessage: string,
    modelId: string,
    maxContextLength: number,
    avatar: string,
    createdTime: string,
    modifiedTime: string
}

export const getEmptyChatCharacter = (): ChatCharacter => {
    return {
        authorUid: 0,
        avatar: "",
        createdTime: "0",
        description: "",
        greetingMessage: "",
        id: "0",
        maxContextLength: 0,
        modelId: "",
        modifiedTime: "0",
        name: "",
        prompt: ""
    }
}

export interface SaveChatCharacterDTO {
    id?: string,
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

export async function deleteChatCharacter(characterId: number) {
    return await doPost("/api/character/delete", {...userTokenHeader(), ...applicationFormUrlEncodedHeader}, { id: characterId })
}