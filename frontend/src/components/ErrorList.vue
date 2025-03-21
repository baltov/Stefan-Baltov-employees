<template>
  <div v-if="file" class="bg-white shadow rounded-lg p-6">
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-lg font-semibold">Errors for "{{ file.fileName }}"</h2>
      <!-- Hide Errors Button -->
      <button
        @click="$emit('hideErrors')"
        class="bg-gray-500 text-white px-3 py-2 rounded-md hover:bg-gray-600"
      >
        Hide Errors
      </button>
    </div>

    <table class="min-w-full divide-y divide-gray-200">
      <thead class="bg-gray-50">
        <tr>
          <th scope="col" class="px-6 py-3 text-left text-sm font-medium text-gray-500 uppercase tracking-wider">Message</th>
          <th scope="col" class="px-6 py-3 text-left text-sm font-medium text-gray-500 uppercase tracking-wider">Line Number</th>
          <th scope="col" class="px-6 py-3 text-left text-sm font-medium text-gray-500 uppercase tracking-wider">Created</th>
        </tr>
      </thead>
      <tbody class="bg-white divide-y divide-gray-200">
        <tr v-for="er in errors" :key="er.lineNumber">
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-800">{{ er.message }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-800">{{ er.lineNumber }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-800">{{ new Date(er.created).toLocaleString() }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import type {PropType} from "vue";
import type {ErrorDto, FileUploadDto} from "../types.ts";

export default defineComponent({
  props: {
    file: {
      type: Object as PropType<FileUploadDto>, // Adjusted for proper type
      required: true,
    },
    errors: {
      type: Array as PropType<ErrorDto[]>, // Properly typing `errors` as an array of `ErrorDto`
      required: true,
    },
  },
});
</script>
