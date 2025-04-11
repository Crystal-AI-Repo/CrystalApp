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
        newCharacter: {
            characterName: "Name",
            characterNameTips: "This is the name for this character",
            characterDescription: "Description",
            characterDescriptionTips: "Description of this character",
            characterPrompt: "Prompt",
            characterPromptTips: "Write all the character settings here",
            characterGreeting: "Greeting Message",
            characterGreetingTips: "The first message the character sends to the user",
            button: {
                submit: "Submit"
            }
        },
        models: {
            modelName: "Model Name",
            modelQualifiedName: "Qualified Name",
            contextLength: "Context Length",
            operations: "Operations",
            addModel: "Add Model",
            button: {
                add: "Add",
                refresh: "Refresh"
            },
            text: {
                emptyModelName: "Model Name could not be empty",
                emptyModelQualifiedName: "Qualified Name could not be empty",
                invalidContextLength: "Context Length should large than 0",
                modelSavedSuccessfully: "Model {modelName} saved successfully",
                deleteModel: "Do you want to delete the model {modelName} ({ modelQualifiedName }) ?",
                modelDeleted: "Model {modelName} deleted successfully"
            }
        }
    }
}