// stores/userStore.js
import { defineStore } from 'pinia'
import type {ChatCharacter} from "@/net/api/chat-character-controller.ts";
import {getChatCharacterDetails} from "@/net/api/chat-character-controller.ts";
import type {Ref} from "vue";
import {watchEffect} from "vue";

export interface ChatCharacterState {
    loading: boolean
    data: ChatCharacter | null
    error: string | null
}

export function useChatCharacterCache(userIds: Ref<string[]>) {
    const store = useChatCharacterStore()

    watchEffect(async () => {
        if (userIds.value.length > 0) {
            await store.fetchUserProfiles(userIds.value)
        }
    })

    function getState(userId: string): ChatCharacterState {
        return store.getProfile(userId)
    }

    return { getChatCharacterState: getState }
}

export const useChatCharacterStore = defineStore('user', {
    state: () => ({
        chatCharacterMap: new Map<string, ChatCharacterState>(),
    }),
    actions: {
        async fetchUserProfiles(ids: string[]) {
            const idsToFetch = ids.filter(id => {
                const existing = this.chatCharacterMap.get(id)
                return !existing?.data && !existing?.loading
            })

            idsToFetch.forEach(id => {
                this.chatCharacterMap = new Map(this.chatCharacterMap.set(id, {
                    loading: true,
                    data: null,
                    error: null
                }))
            })

            await Promise.all(idsToFetch.map(async id => {
                try {
                    const data = await getChatCharacterDetails(id)

                    this.chatCharacterMap = new Map(this.chatCharacterMap.set(id, {
                        loading: false,
                        data,
                        error: null
                    }))
                } catch (error) {
                    this.chatCharacterMap = new Map(this.chatCharacterMap.set(id, {
                        loading: false,
                        data: null,
                        error: error instanceof Error ? error.message : 'Unknown error'
                    }))
                }
            }))
        }
    },
    getters: {
        getProfile: (state) => {
            return (userId: string) => state.chatCharacterMap.get(userId) || {
                loading: false,
                data: null,
                error: null
            }
        }
    }
})