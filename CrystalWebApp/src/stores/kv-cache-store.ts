import {ref} from 'vue'
import {defineStore} from 'pinia'
import type {User} from "@/net/api/user-controller.ts";
import {getUserProfile} from "@/net/api/user-controller.ts";
import type {ChatCharacter} from "@/net/api/chat-character-controller.ts";
import {getChatCharacterDetails} from "@/net/api/chat-character-controller.ts";

export const useKVCacheStore = defineStore('kv-cache-store', () => {
    const userProfiles = ref<Map<string, User>>(new Map())
    const chatCharacters = ref<Map<string, ChatCharacter>>(new Map())

    async function getUserProfileByUid(uid: string) {
        if (!userProfiles.value.has(uid)) {
            // Prevent multi-request for the same user
            // @ts-ignore
            userProfiles.value.set(uid, {})
            userProfiles.value.set(uid, await getUserProfile(uid))
        }
    }

    async function getChatCharacterById(id: string) {
        if (!chatCharacters.value.has(id)) {
            // Prevent multi-request for the same user
            // @ts-ignore
            chatCharacters.value.set(id, {})
            chatCharacters.value.set(id, await getChatCharacterDetails(id))
        }
    }

    return { userProfiles, getUserProfileByUid, chatCharacters, getChatCharacterById }
})