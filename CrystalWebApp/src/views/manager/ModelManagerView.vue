<script setup lang="ts">

import {reactive, ref} from "vue";
import type {Model} from "@/net/model.ts";
import {deleteModel, getAllModels, saveModel} from "@/net/model.ts";
import {useI18n} from "vue-i18n";
import {Delete, Edit} from "@element-plus/icons-vue";
import type {FormInstance, FormRules} from "element-plus";
import {showSimpleDialog} from "@/utils/dialog-utils.ts";

const { t } = useI18n()

const models = ref<Model[]>([])

const refreshing = ref(true)

async function refreshData() {
  refreshing.value = true
  models.value = await getAllModels()
  refreshing.value = false
}

refreshData()

const emptyModelData = () => {
  return {
    contextLength: 1024,
    displayName: "",
    qualifiedName: ""
  }
}
const modelEditDialogVisible = ref(false)
// 0 Add 1 Edit
const modelEditDialogMode = ref(0)
const modelEditDialogData = ref<Model>(emptyModelData())
const modelEditDialogForm = ref<FormInstance>()

function addModelButton() {
  modelEditDialogData.value = emptyModelData()
  modelEditDialogMode.value = 0
  modelEditDialogVisible.value = true
}

function editModelButton(index: number, model: Model) {
  modelEditDialogData.value = model
  modelEditDialogMode.value = 1
  modelEditDialogVisible.value = true
}

function cancelEditModelButton() {
  modelEditDialogVisible.value = false
  modelEditDialogData.value = emptyModelData()
  modelEditDialogMode.value = 0
}

async function editModelDialogConfirmed(formEl: FormInstance | undefined) {
  if (!formEl) return
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      if (await saveModel(modelEditDialogData.value)) {
        ElMessage.success(t('manager.models.text.modelSavedSuccessfully', { modelName: modelEditDialogData.value.displayName }))
        modelEditDialogVisible.value = false
        await refreshData()
      }
    }
  })
}

function deleteModelButton(index: number, model: Model) {
  showSimpleDialog(
      t,
      t('dialog.warning'),
      t(
          'manager.models.text.deleteModel',
          {
            modelName: model.displayName,
            modelQualifiedName: model.qualifiedName
          }
      ),
      async () => {
        if (await deleteModel(model)) {
          ElMessage.success(t('manager.models.text.modelDeleted', { modelName: model.displayName }))
          await refreshData()
        }
      }
  )
}

const modelFormRules = reactive<FormRules<Model>>({
  displayName: [
    { required: true, message: t('manager.models.text.emptyModelName'), trigger: 'blur'}
  ],
  qualifiedName: [
    { required: true, message: t('manager.models.text.emptyModelQualifiedName'), trigger: 'blur'}
  ],
  contextLength: [
    { required: true, message: t('manager.models.text.invalidContextLength'), trigger: 'blur'}
  ],
})
</script>

<template>
  <main class="page-container">
    <div class="flex flex-horizontal flex-center-vertically">
      <el-button
          size="default"
          type="info"
          plain
          @click="refreshData()"
      >
        {{ t('manager.models.button.refresh') }}
      </el-button>
      <el-button
          size="default"
          type="primary"
          plain
          @click="addModelButton()"
      >
        {{ t('manager.models.button.add') }}
      </el-button>
    </div>

    <el-table :data="models" style="width: 100%" size="default" v-loading="refreshing">
      <el-table-column prop="displayName" :label="t('manager.models.modelName')"  />
      <el-table-column prop="qualifiedName" :label="t('manager.models.modelQualifiedName')" />
      <el-table-column prop="contextLength" :label="t('manager.models.contextLength')" width="180" />
      <el-table-column :label="t('manager.models.operations')">
        <template #default="scope">
          <el-button
              :icon="Edit"
              size="small"
              type="primary"
              @click="editModelButton(scope.$index, scope.row)"
              plain=""
          />
          <el-button
              :icon="Delete"
              size="small"
              type="danger"
              @click="deleteModelButton(scope.$index, scope.row)"
              plain=""
          />
        </template>
      </el-table-column>
    </el-table>

    <!-- Dialog: Add new model -->
    <el-dialog v-model="modelEditDialogVisible" :title="modelEditDialogMode == 0 ? t('manager.models.addModel') : modelEditDialogData.displayName" width="500">
      <el-form ref="modelEditDialogForm" :model="modelEditDialogData" label-position="top" size="default" :rules="modelFormRules">
        <el-form-item :label="t('manager.models.modelName')" prop="displayName">
          <el-input v-model="modelEditDialogData.displayName" maxlength="32" show-word-limit />
        </el-form-item>
        <el-form-item :label="t('manager.models.modelQualifiedName')" prop="qualifiedName">
          <el-input v-model="modelEditDialogData.qualifiedName" maxlength="64" show-word-limit />
        </el-form-item>
        <el-form-item :label="t('manager.models.contextLength')" prop="contextLength">
          <el-input-number v-model="modelEditDialogData.contextLength" :step="1024" min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="flex flex-horizontal flex-center-vertically">
          <span class="flex-grow-1" />
          <el-button size="default" @click="cancelEditModelButton()">{{ t('dialog.cancel') }}</el-button>
          <el-button size="default" type="primary" @click="editModelDialogConfirmed(modelEditDialogForm)">
            {{ t('dialog.confirm') }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </main>
</template>

<style scoped>
.page-container {
  flex-grow: 1;
}
</style>