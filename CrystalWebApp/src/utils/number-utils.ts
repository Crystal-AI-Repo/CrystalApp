export function formatByteSize(byteSize: number) {
    if (byteSize < 1_000) {
        return byteSize + " B"
    } else if (byteSize >= 1_000 && byteSize < 1_000_000) {
        return (byteSize / 1_000).toFixed(2) + " KB"
    } else if (byteSize >= 1_000_000 && byteSize < 1_000_000_000) {
        return (byteSize / 1_000_000).toFixed(2) + " MB"
    }else if (byteSize >= 1_000_000_000 && byteSize < 1_000_000_000_000) {
        return (byteSize / 1_000_000_000).toFixed(2) + " GB"
    }
}

export function padStart(str: string | number, length: number, padding: string): string {
    const inputLength = str.toString().length
    if (inputLength >= length) {
        return str.toString()
    } else {
        const paddingCount = length - inputLength
        return padding.repeat(paddingCount) + str
    }
}