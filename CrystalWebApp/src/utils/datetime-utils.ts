import {padStart} from "@/utils/number-utils.ts";

export function formatTimestamp(timestamp: number) {
    const date = new Date(timestamp);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hours = date.getHours();
    const minutes = date.getMinutes();
    const seconds = date.getSeconds();

    return `
        ${year}-${padStart(month, 2, '0')}-${padStart(day, 2, '0')}
        ${padStart(hours, 2, '0')}:${padStart(minutes, 2, '0')}:${padStart(seconds, 2, '0')}`
}

export function iso8061Date(str: string): Date {
    return new Date(str)
}