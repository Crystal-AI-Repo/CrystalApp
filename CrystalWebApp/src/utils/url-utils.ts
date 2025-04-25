export function getQueryString(name: string): string {
    const reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
    const r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return unescape(r[2])
    }
    return ''
}

export function getUserAvatarUrl(uid: string) {
    const serverBaseUrl = import.meta.env.VITE_SERVER_BASE_URL
    return serverBaseUrl + '/resource/user/avatar?uid=' + uid
}

export function getCharacterAvatarUrl(id: string) {
    const serverBaseUrl = import.meta.env.VITE_SERVER_BASE_URL
    return serverBaseUrl + '/resource/character/avatar?id=' + id
}