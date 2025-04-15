export default {
    dialog: {
        confirm: "确认",
        cancel: "取消",
        yes: "是",
        no: "否",
        continue: "继续",
        warning: "警告"
    },
    home: {
        header: {
            nav: {
                category: "分类",
                author: "作者",
                rank: "排名"
            },
            avatar: {
                nav: {
                    profile: "个人中心",
                    logout: "退出登录"
                }
            }
        },
        tab: {
            commendations: "推荐作品",
            latest: "最新作品"
        }
    },
    manager: {
        sideNav: {
            profile: "个人资料",
            newCharacter: "创建新角色",
            models: "模型"
        },
        profile: {
            tab: {
                myChats: "我的聊天",
                myCharacters: "我创建的角色"
            },
            myChats: {

            },
            myCharacters: {
                text: {
                    deleteMyChatCharacter: "是否要删除角色 {name}? 如果其他用户已经添加到聊天列表, 此角色将不再可用",
                    chatCharacterContactAdded: "{name} 已添加到聊天列表"
                }
            }
        },
        newCharacter: {
            titleNewCharacter: "创建新角色",
            titleEditCharacter: "编辑 {name}",
            characterName: "名称",
            characterNameTips: "此角色的名称",
            characterDescription: "描述",
            characterDescriptionTips: "此角色的描述",
            characterModel: "模型",
            characterModelTips: "此角色使用的大型语言模型",
            characterPrompt: "提示词",
            characterPromptTips: "在这里写角色的所有设定",
            characterGreeting: "打招呼消息",
            characterGreetingTips: "角色发送给用户的第一句话",
            button: {
                submit: "提交"
            },
            text: {
                emptyCharacterName: "角色名称不能为空",
                emptyCharacterDescription: "角色描述不能为空",
                emptyCharacterPrompt: "提示词不能为空",
                emptyCharacterGreeting: "角色打招呼消息不能为空",
                emptyCharacterModel: "请选择一个模型",
                invalidCharacterId: "无效的角色 Id"
            }
        },
        models: {
            modelName: "模型名称",
            modelQualifiedName: "实际名称",
            contextLength: "上下文长度",
            operations: "操作",
            addModel: "添加模型",
            button: {
                add: "添加",
                refresh: "刷新"
            },
            text: {
                emptyModelName: "模型名称不能为空",
                emptyModelQualifiedName: "模型实际名称不能为空",
                invalidContextLength: "上下文长度必须大于 0",
                modelSavedSuccessfully: "模型 {modelName} 保存成功",
                deleteModel: "是否要删除模型 {modelName} ({ modelQualifiedName }) ?"
            }
        }
    }
}
