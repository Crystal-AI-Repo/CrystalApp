import {doGet, doPost} from "@/net/axios-request.ts";
import {userTokenHeader} from "@/utils/auth-utils.ts";

export interface User {
    id: number,
    username: string,
    nickname: string,
    email: string,
    avatar: string,
    registeredTime: number,
    modifiedTime: number,
    activated: boolean
}

export interface UserProfileUpdateDTO {
    nickname: string
}

export async function getUserProfile(uid: number) {
    return (await doGet<User>("/api/user/profile", {}, { uid: uid })).data
}

export async function getMyProfile() {
    return (await doGet<User>("/api/user/myProfile", {...userTokenHeader()}, {})).data
}

export async function updateMyProfile(dto: UserProfileUpdateDTO) {
    return (await doPost<User>("/api/user/myProfile", {...userTokenHeader()}, {...dto})).data
}