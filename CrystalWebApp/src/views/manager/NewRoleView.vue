<script setup lang="ts">

import {ref} from "vue";
import {useI18n} from "vue-i18n";

const { t } = useI18n()

interface NewRoleForm {
  roleName: string,
  roleDescription: string,
  rolePrompt: string,
  roleGreeting: string
}

const form = ref<NewRoleForm>({
  roleName: "",
  roleDescription: "",
  roleGreeting: "",
  rolePrompt: "",
})

</script>

<template>
  <main class="page-container">
    <el-form :model="form" label-width="auto" size="default" label-position="top">
      <!-- Role Name -->
      <el-form-item :label="t('manager.newCharacter.characterName')">
        <el-input v-model="form.roleName" maxlength="64" show-word-limit />
      </el-form-item>
      <el-alert class="tips" type="info" show-icon :closable="false">
        <p>{{ t('manager.newCharacter.characterNameTips') }}</p>
      </el-alert>

      <!-- Role Description -->
      <el-form-item :label="t('manager.newCharacter.characterDescription')">
        <el-input v-model="form.roleDescription" type="textarea" />
      </el-form-item>
      <el-alert class="tips" type="info" show-icon :closable="false">
        <p>{{ t('manager.newCharacter.characterDescriptionTips') }}</p>
      </el-alert>

      <!-- Role Prompt -->
      <el-form-item :label="t('manager.newCharacter.characterPrompt')">
        <el-input v-model="form.rolePrompt" type="textarea" />
      </el-form-item>
      <el-alert class="tips" type="info" show-icon :closable="false">
        <p>{{ t('manager.newCharacter.characterPromptTips') }}</p>
      </el-alert>

      <!-- Role Greeting -->
      <el-form-item :label="t('manager.newCharacter.characterGreeting')">
        <el-input v-model="form.roleGreeting" />
      </el-form-item>
      <el-alert class="tips" type="info" show-icon :closable="false">
        <p>{{ t('manager.newCharacter.characterGreetingTips') }}</p>
      </el-alert>

      <!-- Buttons -->
      <div class="flex flex-horizontal flex-center-vertically">
        <el-button type="primary">{{ t('manager.newCharacter.button.submit') }}</el-button>
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