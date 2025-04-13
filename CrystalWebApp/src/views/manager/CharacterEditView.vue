<script setup lang="ts">

import {onMounted, reactive, ref, watch} from "vue";
import {useI18n} from "vue-i18n";
import type {Model} from "@/net/api/model.ts";
import {getAllModels, saveModel} from "@/net/api/model.ts";
import {FormInstance, FormRules} from "element-plus";
import {getQueryString} from "@/utils/url-utils.ts";
import {ChatCharacter, getChatCharacterDetails, saveChatCharacter} from "@/net/api/chat-character-controller.ts";
import router from "@/router";
import {ArrowLeft} from "@element-plus/icons-vue";

const { t } = useI18n()

/**
 * Form
 */
interface NewRoleForm {
  characterName: string,
  characterDescription: string,
  characterPrompt: string,
  characterGreeting: string,
  characterModel: string
}

const idFromDataLoading = ref(true)
const formRef = ref<FormInstance>()
const form = ref<NewRoleForm>({
  characterName: "",
  characterDescription: "",
  characterGreeting: "",
  characterPrompt: "",
  characterModel: ""
})


const mode = ref(getQueryString("mode"))
const editingCharacter = ref<ChatCharacter>()
if (!mode.value) {
  mode.value = "new"
  idFromDataLoading.value = false
} else if (mode.value == "edit") {
  idFromDataLoading.value = true

  const characterId = getQueryString("id")
  if (!characterId) {
    ElMessage.error(t('manager.newCharacter.text.invalidCharacterId'))
  }

   getChatCharacterDetails(Number.parseInt(characterId)).then((res) => {
     idFromDataLoading.value = false
     editingCharacter.value = res
     form.value = {
       characterName: res.name,
       characterDescription: res.description,
       characterGreeting: res.greetingMessage,
       characterPrompt: res.prompt,
       characterModel: res.modelId
     }
   }).catch((err) => {
     idFromDataLoading.value = true
   })
}

const formRules = reactive<FormRules<Model>>({
  characterName: [
    { required: true, message: t('manager.newCharacter.text.emptyCharacterName'), trigger: 'blur'}
  ],
  characterDescription: [
    { required: true, message: t('manager.newCharacter.text.emptyCharacterDescription'), trigger: 'blur'}
  ],
  characterGreeting: [
    { required: true, message: t('manager.newCharacter.text.emptyCharacterGreeting'), trigger: 'blur'}
  ],
  characterPrompt: [
    { required: true, message: t('manager.newCharacter.text.emptyCharacterPrompt'), trigger: 'blur'}
  ],
  characterModel: [
    { required: true, message: t('manager.newCharacter.text.emptyCharacterModel'), trigger: 'blur'}
  ]
})

const availableModels = ref<Model[]>([])

onMounted(async () => {
  availableModels.value = await getAllModels()
})

const autoCorrectSelectedModel = () => {
  /**
   * If the selected model is id instead of qualifiedName,
   * get the qualifiedName after availableModels updated
   */
  if (form.value.characterModel) {
    const t: Model | undefined = availableModels.value.find(e => e.id === Number.parseInt(form.value.characterModel))
    if (t !== undefined) {
      form.value.characterModel = t.qualifiedName
    }
  }
}

watch(form, () => {
  autoCorrectSelectedModel()
})
watch(availableModels, () => {
  autoCorrectSelectedModel()
})

async function submit(formEl: FormInstance | undefined) {
  if (!formEl) return
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      const result = await saveChatCharacter(
          {
            ...(mode.value == 'edit' ? { id: Number.parseInt(getQueryString('id')) } : {}),
            ...{
              description: form.value.characterDescription,
              greeting: form.value.characterGreeting,
              model: form.value.characterModel,
              name: form.value.characterName,
              prompt: form.value.characterPrompt
            }
          })

      if (result.code == 200) {
        await router.push("/manager/profile/myCharacters")
      }
    }
  })
}

</script>

<template>
  <main class="page-container">
    <div class="flex flex-horizontal flex-center-vertically">
      <el-button v-if="mode == 'edit'" class="mr-4" :icon="ArrowLeft" circle size="default" @click="router.push('/manager/profile/myCharacters')" />
      <p class="text-lg" v-if="mode == 'new'">{{ t('manager.newCharacter.titleNewCharacter') }}</p>
      <p class="text-lg" v-else>{{ t('manager.newCharacter.titleEditCharacter', { name: editingCharacter?.name }) }}</p>
    </div>

    <el-form class="mt-4" ref="formRef" :model="form" label-width="auto" size="default" label-position="top" :rules="formRules" v-loading="idFromDataLoading">
      <!-- Role Name -->
      <el-form-item :label="t('manager.newCharacter.characterName')" prop="characterName">
        <el-input v-model="form.characterName" maxlength="64" show-word-limit />
      </el-form-item>
      <el-alert class="tips" type="info" show-icon :closable="false">
        <p>{{ t('manager.newCharacter.characterNameTips') }}</p>
      </el-alert>

      <!-- Role Description -->
      <el-form-item :label="t('manager.newCharacter.characterDescription')" prop="characterDescription">
        <el-input v-model="form.characterDescription" type="textarea" />
      </el-form-item>
      <el-alert class="tips" type="info" show-icon :closable="false">
        <p>{{ t('manager.newCharacter.characterDescriptionTips') }}</p>
      </el-alert>

      <!-- Role ModelController -->
      <el-form-item :label="t('manager.newCharacter.characterModel')" prop="characterModel">
        <el-select v-model="form.characterModel" placeholder="Select" style="width: 240px">
          <el-option
              v-for="item in availableModels"
              :key="item.qualifiedName"
              :label="item.displayName"
              :value="item.qualifiedName"
          >
            <div class="flex flex-horizontal flex-center-vertically gap-2">
              <span style="font-size: .95rem">{{ item.displayName }}</span>
              <span class="flex-grow-1" />
              <span style="color: gray; font-size: .95rem">{{ item.qualifiedName }}</span>
            </div>

          </el-option>
        </el-select>
      </el-form-item>
      <el-alert class="tips" type="info" show-icon :closable="false">
        <p>{{ t('manager.newCharacter.characterModelTips') }}</p>
      </el-alert>

      <!-- Role Prompt -->
      <el-form-item :label="t('manager.newCharacter.characterPrompt')" prop="characterPrompt">
        <el-input v-model="form.characterPrompt" type="textarea" />
      </el-form-item>
      <el-alert class="tips" type="info" show-icon :closable="false">
        <p>{{ t('manager.newCharacter.characterPromptTips') }}</p>
      </el-alert>

      <!-- Role Greeting -->
      <el-form-item :label="t('manager.newCharacter.characterGreeting')" prop="characterGreeting">
        <el-input v-model="form.characterGreeting" />
      </el-form-item>
      <el-alert class="tips" type="info" show-icon :closable="false">
        <p>{{ t('manager.newCharacter.characterGreetingTips') }}</p>
      </el-alert>

      <!-- Buttons -->
      <div class="flex flex-horizontal flex-center-vertically">
        <el-button type="primary" @click="submit(formRef)">{{ t('manager.newCharacter.button.submit') }}</el-button>
      </div>
    </el-form>
  </main>

</template>

<style scoped>
.page-container {
  flex-grow: 1;
}

.tips {
  margin-bottom: 1rem;
}
</style>