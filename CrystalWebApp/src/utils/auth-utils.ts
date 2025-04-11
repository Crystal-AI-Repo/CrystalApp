export interface UserAuthToken {
    accessToken: string,
    refreshToken: string,
    expires: number
}

export function getUserAuthToken(): UserAuthToken | null {
    const a = localStorage.getItem("ticket")
    const b = localStorage.getItem("r_ticket")
    const c = localStorage.getItem("ticket_expires")

    if (!a || !b || !c) {
        return null
    }

    return {
        accessToken: a,
        refreshToken: b,
        expires: Number.parseInt(c),
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