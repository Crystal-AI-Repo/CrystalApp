import type {AxiosResponse} from 'axios';
import axios from 'axios';
// @ts-ignore
import type {Readable} from 'stream';
import type {OpenAIStreamChunk, OpenAIStreamRequest} from "@/data/open-api.ts";

export class AxiosOpenApiRequest<REQUEST = OpenAIStreamRequest> {
    private readonly url: string
    private readonly token: string | null
    private readonly contentType: string

    constructor(url: string, token: string | null, contentType?: string) {
        this.url = url
        this.token = token
        if (contentType == undefined) {
            this.contentType = 'application/json'
        } else {
            this.contentType = contentType
        }
    }


    public async *axiosStreamToAsyncIterator(stream: Readable): AsyncGenerator<string> {
        let t = ''
        for await (const chunk of stream) {
            const v = chunk.toString()
            if (v == '\n') {
                if (t.replace('\\s+', '').length > 0) {
                    yield t.replace(/^data:/, '').replace(/^data: /, '').replace('\s+', '')
                }
                t = ''
            } else {
                t += v
            }
        }
    }

    public async *streamToAsyncIterator(stream: ReadableStreamDefaultReader<Uint8Array<ArrayBufferLike>>): AsyncGenerator<string> {
        while (true) {
            const { done, value } = await stream.read()
            if (done) break
            const str = new TextDecoder().decode(value).trim().replace(/^data:/, '')
            if (str != '' && !str.toLowerCase().includes('[done]')) {
                const lines = str.split("\n\n")
                for (let i = 0; i < lines.length; i++) {
                    const e = lines[i]
                    yield e.trim().replace(/^data:/, '')
                }
            }
        }
    }

    public async *sendMessage(request: REQUEST, onException?: (error: any) => void): AsyncGenerator<OpenAIStreamChunk> {
        try {
            const response = await fetch(this.url, {
                method: 'POST',
                headers: {
                    'Content-Type': this.contentType,
                    ...(this.token == null ? {} : { 'Authorization': `Bearer ${this.token}` })
                },
                body: JSON.stringify(request),
            })

            const reader = response.body!!.getReader()
            for await (const line of this.streamToAsyncIterator(reader)) {
                const parsed = JSON.parse(line) as OpenAIStreamChunk;
                yield parsed
            }

            //for await (const chunk of this.streamToAsyncIterator(response.data)) {
            //    const str = chunk.toString()
            //    if (!str.toLowerCase().includes('[done]')) {
            //        const parsed = JSON.parse(chunk.toString()) as OpenAIStreamChunk
            //        yield parsed
            //    }
            //}
        } catch (error: any) {
            if (axios.isAxiosError(error)) {
                console.error(error)
                ElMessage.error(error.code + ": " + error.message)
            } else {
                console.error(error)
                ElMessage.error("Unknown Error: " + error.message)
            }
            if (onException != null) {
                onException(error)
            }
        }
    }
}