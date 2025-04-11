export function getColorLerp(from: number[], to: number[], alpha: number): number[] {
    const r1 = from[0]
    const g1 = from[1]
    const b1 = from[2]

    const r2 = to[0]
    const g2 = to[1]
    const b2 = to[2]

    return [Math.trunc(r1 + alpha * (r2 - r1)), Math.trunc(g1 + alpha * (g2 - g1)), Math.trunc(b1 + alpha * (b2 - b1))]
}

export function getCenterColors(from: number[], to: number[], numOfColors: number): number[][] {
    const delta = 1.0 / (numOfColors - 1)
    const arr = []
    for (let i = 0; i < numOfColors; i++) {
        arr.push(getColorLerp(from, to, i * delta))
    }
    return arr
}

export function toHexColor(rgb: number[]): string {
    return rgb[0].toString(16) + rgb[1].toString(16) + rgb[2].toString(16)
}