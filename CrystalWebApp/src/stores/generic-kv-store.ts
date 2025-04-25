import {defineStore} from "pinia";
import type {User} from "@/net/api/user-controller.ts";
import {getUserProfile} from "@/net/api/user-controller.ts";
import type {Ref} from "vue";
import {watchEffect} from "vue";
import type {ChatCharacter} from "@/net/api/chat-character-controller.ts";
import {getChatCharacterDetails} from "@/net/api/chat-character-controller.ts";

export interface StoreDataState<T> {
    loading: boolean
    data: T | null
    error: string | null
}

export class KeyValueStore<K = string, V = any> {
    private readonly storeName: string
    private readonly getDataIfNotExist: (key: K) => Promise<V | null>

    constructor(storeName: string, getDataIfNotExist: (key: K) => Promise<V | null>) {
        this.storeName = storeName
        this.getDataIfNotExist = getDataIfNotExist
    }


    public use() {
        const getDataIfNotExist = (id: K) => {
            return this.getDataIfNotExist(id)
        }

        return defineStore(this.storeName, {
            state: () => ({
                dataMap: new Map<K, StoreDataState<V>>(),
            }),
            actions: {
                async fetchData(ids: K[]) {
                    const idsToFetch = ids.filter(id => {
                        const existing = this.dataMap.get(id)
                        return !existing?.data && !existing?.loading
                    })

                    idsToFetch.forEach(id => {
                        this.dataMap = new Map(this.dataMap.set(id, {
                            loading: true,
                            data: null,
                            error: null
                        }))
                    })

                    await Promise.all(idsToFetch.map(async id => {
                        const onError = (error: any | null, message: string = '') => {
                            this.dataMap = new Map(this.dataMap.set(id, {
                                loading: false,
                                data: null,
                                error: message != '' ? message : error instanceof Error ? error.message : 'Unknown Error'
                            }))
                        }

                        try {
                            const data: V | null = await getDataIfNotExist(id)

                            if (data != null) {
                                this.dataMap = new Map(this.dataMap.set(id, {
                                    loading: false,
                                    // @ts-ignore
                                    data: data,
                                    error: null
                                }))
                            } else {
                                onError(null, 'Empty Response')
                            }
                        } catch (error) {
                            onError(error)
                        }
                    }))
                }
            },
            getters: {
                getData: (state) => {
                    return (id: K) => (state.dataMap.get(id) || {
                        loading: false,
                        data: null,
                        error: null
                    }) as StoreDataState<V>
                }
            }
        })()
    }

    public useCache<F extends string>(funcName: F, ids: Ref<K[]>): { [Key in F]: (id: K) => StoreDataState<V> } {
        const store = this.use()

        watchEffect(async () => {
            if (ids.value.length > 0) {
                await store.fetchData(ids.value)
            }
        })

        function getState(id: K): StoreDataState<V> {
            return store.getData(id)
        }

        return { [funcName]: getState } as { [Key in F]: (id: K) => StoreDataState<V> }
    }
}

export const userProfileStore = new KeyValueStore<string, User>(
    'user',
    async (key: string) => {
        try {
            return await getUserProfile(key)
        } catch (e) {
            return null
        }
    }
)

export const chatCharacterStore = new KeyValueStore<string, ChatCharacter>(
    'chat-character',
    async (key: string) => {
        try {
            return await getChatCharacterDetails(key)
        } catch (e) {
            return null
        }
    }
)