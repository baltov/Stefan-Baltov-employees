<template>
  <div v-if="file" class="bg-white shadow rounded-lg p-6">
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-lg font-semibold">Results for "{{ file.fileName }}"</h2>
      <button
          @click="$emit('hideResults')"
      class="bg-red-500 text-white px-3 py-2 rounded-md hover:bg-red-600"
      >
      Hide Results
      </button>
    </div>

    <table class="min-w-full divide-y divide-gray-200">
      <thead class="bg-gray-50">
      <tr>
        <th scope="col" class="px-6 py-3 text-left text-sm font-medium text-gray-500 uppercase tracking-wider">
          Project ID
        </th>
        <th scope="col" class="px-6 py-3 text-left text-sm font-medium text-gray-500 uppercase tracking-wider">
          Employee ID #1
        </th>
        <th scope="col" class="px-6 py-3 text-left text-sm font-medium text-gray-500 uppercase tracking-wider">
          Employee ID #2
        </th>
        <th scope="col" class="px-6 py-3 text-left text-sm font-medium text-gray-500 uppercase tracking-wider">
          Days worked
        </th>
      </tr>
      </thead>
      <tbody class="bg-white divide-y divide-gray-200">
      <tr v-for="result in results" :key="result.projectId">
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-800">{{ result.projectId }}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-800">{{ result.oneUserId }}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-800">{{ result.secondUserId }}</td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-800">{{ result.days }}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script lang="ts">
import {defineComponent, type PropType} from 'vue';
import type { FileUploadDto, ReportDto} from "../types.ts";

export default defineComponent({
  props: {
    file: {
      type: Object as PropType<FileUploadDto>,
      required: true,
    },
    results: {
      type: Array as PropType<ReportDto[]>,
      required: true,
    },
  },
});
</script>