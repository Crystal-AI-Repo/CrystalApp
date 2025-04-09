import { createI18n } from "vue-i18n";
import zhCn from "./zh-cn";
import en from "./en";

const i18n = createI18n({
    locale: localStorage.getItem("language") || "zhCn",
    globalInjection: true,
    legacy: false,
    messages: { zhCn, en }
})

export default i18n;
