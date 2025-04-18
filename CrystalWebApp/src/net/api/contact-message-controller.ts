export interface ChatHistoryMessage {
    id: number,
    senderType: number,
    sender: number,
    messageType: number,
    message: string,
    createdTime: number,
    revoked: boolean
}