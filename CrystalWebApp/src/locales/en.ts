export default {
    dialog: {
        confirm: "Confirm",
        cancel: "Cancel",
        yes: "Yes",
        no: "No",
        continue: "Continue",
        warning: "Warning"
    },
    home: {
        header: {
            nav: {
                category: "Category",
                author: "Author",
                rank: "Rank"
            },
            avatar: {
                nav: {
                    profile: "Profile",
                    logout: "Log Out"
                }
            }
        },
        tab: {
            commendations: "Commendations",
            latest: "Latest"
        }
    },
    manager: {
        sideNav: {
            profile: "Profile",
            newCharacter: "New Character",
            models: "Models"
        },
        profile: {
            tab: {
                myChats: "My Chats",
                myCharacters: "My Characters"
            },
            myChats: {

            },
            myCharacters: {
                text: {
                    deleteMyChatCharacter: "Do you want to delete character {name}? If someone has added to contact, the character will be unavailable.",
                    chatCharacterContactAdded: "{name} has been added to your contact list"
                }
            }
        },
        newCharacter: {
            titleNewCharacter: "New Character",
            titleEditCharacter: "Edit {name}",
            characterName: "Name",
            characterNameTips: "This is the name for this character",
            characterDescription: "Description",
            characterDescriptionTips: "Description of this character",
            characterModel: "Model",
            characterModelTips: "The model used by this character",
            characterPrompt: "Prompt",
            characterPromptTips: "Write all the character settings here",
            characterGreeting: "Greeting Message",
            characterGreetingTips: "The first message the character sends to the user",
            characterPrivacy: "Privacy",
            characterPrivacyTips: "Publish to everyone or visible only yourself",
            button: {
                submit: "Submit"
            },
            text: {
                emptyCharacterName: "Character name could not be empty",
                emptyCharacterDescription: "Character description could not be empty",
                emptyCharacterPrompt: "Character prompt could not be empty",
                emptyCharacterGreeting: "Character greeting message could not be empty",
                emptyCharacterModel: "Please select a model",
                invalidCharacterId: "Invalid character id",
                private: "Private",
                public: "Public"
            }
        },
        models: {
            modelName: "ModelController Name",
            modelQualifiedName: "Qualified Name",
            contextLength: "Context Length",
            operations: "Operations",
            addModel: "Add ModelController",
            button: {
                add: "Add",
                refresh: "Refresh"
            },
            text: {
                emptyModelName: "ModelController Name could not be empty",
                emptyModelQualifiedName: "Qualified Name could not be empty",
                invalidContextLength: "Context Length should large than 0",
                modelSavedSuccessfully: "ModelController {modelName} saved successfully",
                deleteModel: "Do you want to delete the model {modelName} ({ modelQualifiedName }) ?",
                modelDeleted: "ModelController {modelName} deleted successfully"
            }
        }
    },
    chat: {
        button: {
            exploreWorkshop: "Explore Workshop"
        },
        text: {
            emptySession: "Explore. Connect. Begin."
        }
    }
}