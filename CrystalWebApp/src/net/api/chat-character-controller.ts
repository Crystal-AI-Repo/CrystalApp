import {applicationFormUrlEncodedHeader, doGet, doPost, multiPartFormDataHeader} from "@/net/axios-request.ts";
import {userTokenHeader} from "@/utils/auth-utils.ts";
import type {PagedData} from "@/net/PagedData.ts";

export interface ChatCharacter {
    id: string,
    authorUid: string,
    name: string,
    description: string,
    prompt: string,
    greetingMessage: string,
    modelId: string,
    maxContextLength: number,
    avatar: string,
    background: string,
    createdTime: string,
    modifiedTime: string,
    privacy: boolean
}

export const getEmptyChatCharacter = (): ChatCharacter => {
    return {
        authorUid: "0",
        avatar: "",
        background: "",
        createdTime: "0",
        description: "",
        greetingMessage: "",
        id: "0",
        maxContextLength: 0,
        modelId: "",
        modifiedTime: "0",
        name: "",
        prompt: "",
        privacy: true
    }
}

export interface SaveChatCharacterDTO {
    id?: string,
    name: string,
    description: string,
    model: string,
    prompt: string,
    greeting: string,
    privacy: boolean,
    avatar: File | null,
    background: File | null
}

export async function getChatCharacterDetails(characterId: string) {
    return (await doGet<ChatCharacter>("/api/character/details", {...userTokenHeader()}, { id: characterId })).data
}

export async function getRecentChatCharacters(page: number) {
    return (await doGet<PagedData<ChatCharacter>>("/api/character/recentCharacters", {}, { page: page })).data
}

export async function getMyCreatedChatCharacters(page: number) {
    return (await doGet<PagedData<ChatCharacter>>("/api/character/myCharacters", {...userTokenHeader()}, { page: page })).data
}

export async function saveChatCharacter(dto: SaveChatCharacterDTO) {
    const formData = new FormData();

    Object.entries(dto).forEach(([key, value]) => {
        if (key !== 'avatar' && value !== undefined) {
            formData.append(key, typeof value === 'object' ? JSON.stringify(value) : value.toString());
        }
    })

    if (dto.avatar instanceof File) {
        formData.append('avatar', dto.avatar)
    }

    if (dto.background instanceof File) {
        formData.append('background', dto.background)
    }

    return await doPost(
        "/api/character/save",
        {...userTokenHeader(), ...multiPartFormDataHeader},
        {...dto}
    )
}

export async function deleteChatCharacter(characterId: string) {
    return await doPost("/api/character/delete", {...userTokenHeader(), ...applicationFormUrlEncodedHeader}, { id: characterId })
}