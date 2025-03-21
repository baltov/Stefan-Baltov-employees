<template>
  <div class="bg-white shadow rounded-lg p-6 mb-6">
    <h2 class="text-lg font-semibold mb-4">Upload a File</h2>
    <form @submit.prevent="uploadFile">
      <div class="flex items-center gap-4">
        <input
            accept=".csv"
            type="file"
            @change="onFileChange"
            id="file"
            class="w-full border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500"
        />
        <button
            type="submit"
            class="bg-indigo-600 text-white px-4 py-2 rounded-lg shadow hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
        >
          Upload
        </button>
      </div>
    </form>
  </div>
</template>

<script lang="ts">
import axios from "axios";
import { defineComponent } from "vue";

export default defineComponent({
  data() {
    return {
      file: null as File | null, // File type or null
    };
  },
  methods: {
    /**
     * Handles the file input change event and sets the selected file.
     */
    onFileChange(event: Event): void {
      const target = event.target as HTMLInputElement;
      if (target && target.files) {
        this.file = target.files[0]; // Get the first selected file
      } else {
        this.file = null; // Clear the file in case of invalid input
      }
    },

    /**
     * Performs file upload using a FormData object.
     */
    async uploadFile(): Promise<void> {
      if (!this.file) {
        alert("Please select a file to upload.");
        return;
      }

      const formData = new FormData();
      formData.append("file", this.file);

      try {
        const response = await axios.post("/api/upload", formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });

        // Simulating uploaded file success response
        const uploadedFile = {
          id: response.data.id, // Assume response includes a unique file ID
          fileName: response.data.fileName,
          created: response.data.created,
          status: response.data.status,
          result: response.data.result,
        };

        // Emit the uploaded file data to the parent
        this.$emit("fileUploaded", uploadedFile);

        alert("File uploaded successfully!");
      } catch (error) {
        console.error("Error uploading file: ", error);
        alert("An error occurred while uploading the file. Please try again.");
      } finally {
        this.file = null; // Clear the file input after upload
        (this.$refs.fileInput as HTMLInputElement).value = ""; // Reset file input
      }
    },
  },
});
</script>