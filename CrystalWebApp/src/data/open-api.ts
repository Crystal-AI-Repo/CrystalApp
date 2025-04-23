// types/openai.ts
export interface OpenAIMessage {
    role: 'user' | 'assistant' | 'system';
    content: string;
}

export interface OpenAIStreamRequest {
    model: string,
    messages: OpenAIMessage[],
    stream?: boolean,
    temperature?: number,
    max_tokens?: number,
}

export interface OpenAIStreamChunk {
    choices: Array<{
        delta: {
            content?: string;
            role?: string;
        };
        finish_reason: string | null;
        index: number;
    }>;
    created: number;
    id: string;
    model: string;
    object: string;
}