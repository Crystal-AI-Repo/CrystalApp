export interface PagedData<T> {
    total: number,
    pages: number,
    current: number,
    records: T[]
}