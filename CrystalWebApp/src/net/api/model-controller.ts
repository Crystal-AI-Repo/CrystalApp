import {applicationFormUrlEncoded, doGet, doPost} from "@/net/axios-request.ts";
import {userTokenHeader} from "@/utils/auth-utils.ts";

export interface Model {
    id: string,
    displayName: string,
    qualifiedName: string,
    contextLength: number
}

export async function getAllModels() {
    const result = await doGet<Model[]>("/api/model/list", {...userTokenHeader()}, {})
    return result.data
}

export async function saveModel(model: Model) {
    const result = await doPost(
        "/api/model/save",
        {...userTokenHeader(), 'Content-Type': applicationFormUrlEncoded},
        {
            modelName: model.displayName,
            qualifiedName: model.qualifiedName,
            contextLength: model.contextLength
        })
    return result.code == 200
}

export async function deleteModel(model: Model) {
    const result = await doPost(
        "/api/model/delete",
        {...userTokenHeader(), 'Content-Type': applicationFormUrlEncoded},
        {
            qualifiedName: model.qualifiedName
        })
    return result.code == 200
}