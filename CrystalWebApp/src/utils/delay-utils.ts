import {getMyCreatedChatCharacters} from "@/net/api/chat-character-controller.ts";

export class DelayedAction {
    private readonly action: () => void
    private readonly minDelayTimeMillis: number
    private readonly finallyAction: () => void

    constructor(minDelayTimeMillis: number, action: () => void, finallyAction: () => void) {
        this.action = action
        this.minDelayTimeMillis = minDelayTimeMillis
        this.finallyAction = finallyAction
    }

    public start() {
        const startAt = Date.now()

        this.action()

        const endAt = Date.now()
        if (endAt - startAt > this.minDelayTimeMillis) {
            this.finallyAction()
        } else {
            const delta = this.minDelayTimeMillis - endAt + startAt
            setTimeout(() => {
                this.finallyAction()
            }, delta)
        }
    }
}