<script setup lang="ts">

import {onMounted, reactive, ref} from "vue";
import {useI18n} from "vue-i18n";
import type {Model} from "@/net/model.ts";
import {getAllModels, saveModel} from "@/net/model.ts";
import {FormInstance, FormRules} from "element-plus";

const { t } = useI18n()

interface NewRoleForm {
  characterName: string,
  characterDescription: string,
  characterPrompt: string,
  characterGreeting: string,
  characterModel: string
}

const formRef = ref<FormInstance>()
const form = ref<NewRoleForm>({
  characterName: "",
  characterDescription: "",
  characterGreeting: "",
  characterPrompt: "",
  characterModel: ""
})

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

const availableModels = ref<Model[]>()

onMounted(async () => {
  availableModels.value = await getAllModels()
})

async function submit(formEl: FormInstance | undefined) {
  if (!formEl) return
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      console.log(form.value)
    }
  })
}

</script>

<template>
  <main class="page-container">
    <el-form ref="formRef" :model="form" label-width="auto" size="default" label-position="top" :rules="formRules">
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

      <!-- Role Model -->
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