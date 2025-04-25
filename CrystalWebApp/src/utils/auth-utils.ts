import {jwtDecode} from "jwt-decode";

export interface UserAuthToken {
    accessToken: string,
    refreshToken: string,
    expires: number,
    payloads: {
        uid: string
    }
}

export function getUserAuthToken(): UserAuthToken | null {
    const a = localStorage.getItem("ticket")
    const b = localStorage.getItem("r_ticket")
    const c = localStorage.getItem("ticket_expires")

    if (!a || !b || !c) {
        return null
    }

    const decoded = jwtDecode<{ uid: string }>(a);

    return {
        accessToken: a,
        refreshToken: b,
        expires: Number.parseInt(c),
        payloads: {
            uid: decoded.uid
        }
    }
}

export function clearUserAuthToken() {
    localStorage.removeItem("ticket")
    localStorage.removeItem("r_ticket")
    localStorage.removeItem("ticket_expires")
}

export function userTokenHeader() {
  return { 'Authorization': `Bearer ${localStorage.getItem("ticket")}` }
}

export function getUserToken() {
    return localStorage.getItem("ticket")
}