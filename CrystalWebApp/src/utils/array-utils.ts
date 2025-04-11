export function moveRight<T>(array: T[], count: number) {
    if (count >= array.length) {
        count = count - array.length
    }

    if (count == 0) {
        return array
    }

    return [...array.slice(-count).reverse(), ...array.slice(0, array.length - count)]
}