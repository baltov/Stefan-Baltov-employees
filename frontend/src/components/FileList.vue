<template>
  <div v-if="files.length > 0" class="bg-white shadow rounded-lg p-6 mb-6">
    <h2 class="text-lg font-semibold mb-4">Uploaded Files</h2>
    <ul class="divide-y divide-gray-200">
      <li
          v-for="file in files"
          :key="file.id"
          class="p-4 flex justify-between items-center hover:bg-gray-200 rounded-md"
      >
        <!-- Delete Button -->
        <button
            v-if="file.status === 'PROCESSED'"
            @click="deleteFile(file.id)"
            class="bg-red-500 text-white px-2 py-1 rounded-md hover:bg-red-600"
            title="Delete file"
        >
          Delete
        </button>

        <div class="flex flex-col text-left ml-4">
          <!-- Hidden field for storing the file ID -->
          <input type="hidden" :value="file.id" />

          <!-- Show file name -->
          <div class="text-gray-800 font-medium">
            {{ file.fileName }}
          </div>

          <!-- Show human-friendly status -->
          <div
              :class="{
              'text-blue-500': file.status === 'UPLOADED',
              'text-yellow-500': file.status === 'PROCESSING',
              'text-green-500': file.status === 'PROCESSED',
            }"
              class="mt-1"
          >
            Status: {{ getHumanStatus(file.status) }}
          </div>

          <!-- Show created date and time -->
          <div class="text-gray-600 text-sm mt-1">
            Created: {{ formatDateTime(file.created) }}
          </div>
          <div class="text-gray-600 text-sm mt-1">
            Result: {{ file.result }}
          </div>
        </div>

        <div class="space-x-2">
          <!-- Results Button -->
          <button
              v-if="file.status === 'PROCESSED'"
              @click="$emit('viewResults', file)"
              class="bg-blue-500 text-white px-3 py-2 rounded-md hover:bg-blue-600"
          >
            Results
          </button>

          <!-- Errors Button -->
          <button
              v-if="file.status === 'PROCESSED'"
              @click="$emit('viewErrors', file)"
              class="bg-red-500 text-white px-3 py-2 rounded-md hover:bg-red-600"
          >
            Errors
          </button>
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import axios from "axios";
import { defineComponent, type PropType } from "vue";
import type { FileUploadDto } from "../types";

export default defineComponent({
  props: {
    /**
     * A list of uploaded files.
     */
    files: {
      type: Array as PropType<FileUploadDto[]>, // Proper type for an array of FileUploadDto
      required: true,
    },
  },
  methods: {
    /**
     * Formats an ISO 8601 date-time string into a human-readable format.
     *
     * @param {string} isoDateTime - The ISO 8601 date-time string (e.g., 2025-03-20T13:14:28.245).
     * @returns {string} - The formatted date and time.
     */
    formatDateTime(isoDateTime: string): string {
      const date = new Date(isoDateTime); // Parse ISO 8601 string into a Date object

      // Converts the date into a locale-specific string
      return date.toLocaleString(); // E.g., "3/20/2025, 1:14:28 PM" in en-US
    },

    /**
     * Returns a human-readable status text based on file status.
     *
     * @param {FileUploadDto['status']} status - The current status of the file.
     * @returns {string} - A friendly status message.
     */
    getHumanStatus(status: FileUploadDto["status"]): string {
      const statusMap: Record<
          FileUploadDto["status"],
          string
      > = {
        UPLOADED: "The file has been uploaded successfully.",
        PROCESSING: "The file is currently being processed.",
        PROCESSED: "The file has been processed successfully.",
      };

      // Fallback text for unknown statuses
      return statusMap[status] || "Unknown status.";
    },

    /**
     * Deletes a file by its ID from the server.
     *
     * @param {number} fileId - The ID of the file to delete.
     */
    async deleteFile(fileId: number): Promise<void> {
      try {
        const response = await axios.delete(`/api/files/${fileId}`);
        if (response.status === 200) {
          // Emit an event to notify the parent of successful deletion
          this.$emit("fileDeleted", fileId);
        } else {
          console.error(`Failed to delete file with ID ${fileId}.`);
        }
      } catch (error) {
        console.error(`Error deleting file with ID ${fileId}:`, error);
      }
    },
  },
});
</script>