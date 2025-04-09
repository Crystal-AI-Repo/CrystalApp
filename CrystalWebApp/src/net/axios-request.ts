import axios, {AxiosError} from "axios";
import type {AxiosResponse} from "axios";
import {Result} from "@/net/Result.ts";

const debug = true

export const proxyPrefixDispatcher = "/dispatcher-api"
export const apiPrefixDispatcher = proxyPrefixDispatcher + "/api/v1"

export function internalGet<T>(
    url: string,
    headers: object,
    params: object
): Promise<Result<T>> {
    return rawGet<Result<T>>(
        url,
        headers,
        params,
        (it) => {
            return new Result<T>(it.data.code, it.data.message, it.data.data)
        }
    )
}

export function internalPost<T>(
    url: string,
    headers: object,
    params: object
): Promise<Result<T>> {
    return rawPost<Result<T>>(
        url,
        headers,
        params,
        (it) => {
            return new Result<T>(it.data.code, it.data.message, it.data.data)
        }
    )
}

export function rawGet<T>(
    url: string,
    headers: object,
    params: object,
    resultProcessor: (response: AxiosResponse<T>) => T = (it) => {
        return it.data
    }
): Promise<T> {
    return new Promise<T>((resolve, reject) => {
        if (debug) console.log("<== [I] GET: " + url)
        if (debug) console.log("    [I] GET Headers: ", headers)
        if (debug) console.log("    [I] GET Params: ", params)
        axios.get<T>(url,{
            params: params,
            headers: headers
        }).then((res: AxiosResponse<T>) => {
            if (debug) console.log(`==> [I] GET Result of [${url}]: `, res)
            const data = resultProcessor(res)
            resolve(data)
        }).catch((err: AxiosError) => reject({ code: err.response?.status, message: err.message, data: null }))
    })
}

export function rawPost<T>(
    url: string,
    headers: object,
    data: object,
    resultProcessor: (response: AxiosResponse<T>) => T = (it) => {
        return it.data
    }
): Promise<T> {
    return new Promise<T>((resolve, reject) => {
        if (debug) console.log("<== [I] POST: " + url)
        if (debug) console.log("    [I] POST Headers: ", headers)
        if (debug) console.log("    [I] POST Params: ", data)
        axios.post<T>(url, data,{
            headers: headers
        }).then((res: AxiosResponse<T>) => {
            const data = resultProcessor(res)
            if (debug) console.log(`==> [I] POST Result of [${url}]: `, res)
            resolve(data)
        }).catch((err: AxiosError) => reject({ code: err.response?.status, message: err.message, data: null }))
    })
}

export const headerAuthorization = "Authorization"

export const headerContentType = "Content-Type"

export const applicationJson = "application/json"

export const applicationXml = "application/xml"

export const applicationJavascript = "application/javascript"

export const textPlain = "text/plain"

export const textHtml = "text/html"

export const applicationFormUrlEncoded = "application/x-www-form-urlencoded"
