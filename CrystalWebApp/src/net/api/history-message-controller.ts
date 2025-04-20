import {doGet} from "@/net/axios-request.ts";
import {userTokenHeader} from "@/utils/auth-utils.ts";

export interface ChatHistoryMessage {
    id: string,
    senderType: number,
    sender: string,
    messageType: number,
    message: string,
    createdTime: number,
    revoked: boolean,
    // Additional props
    childrenSize: number
}

export interface LocalChatHistoryMessage {
    children: (ChatHistoryMessage & LocalChatHistoryMessage)[]
}

export async function getChatHistoryLeaves(contactId: string) {
    return (await doGet<ChatHistoryMessage[]>("/api/chat-history/leaves", {...userTokenHeader()}, { contactId: contactId }))
}

export async function getChatHistoryUpwards(contactId: string, messageId: string) {
    return (await doGet<ChatHistoryMessage[]>("/api/chat-history/fetch", {...userTokenHeader()}, { contactId: contactId, messageId: messageId }))
}