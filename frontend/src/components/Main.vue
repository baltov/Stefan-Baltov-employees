<template>
  <div class="max-w-3xl mx-auto p-4">
    <!-- Header -->
    <h1 class="text-2xl font-bold text-center mb-6">File Manager</h1>

    <FileUpload @fileUploaded="addFile" />

    <FileList
        :files="files"
        @fileDeleted="handleFileDeleted"
        @viewResults="selectFileAndFetchResults"
        @viewErrors="selectFileAndFetchErrors"
    />

    <ResultList
        v-if="selectedFile && currentView === 'results'"
        :file="selectedFile"
        :results="results"
        @hideResults="hideResults"
    />

    <ErrorList
        v-if="selectedFile && currentView === 'errors'"
        :file="selectedFile"
        :errors="errors"
        @hideErrors="hideErrors"
    />
  </div>
</template>

<script lang="ts">
import FileUpload from "./FileUpload.vue";
import FileList from "./FileList.vue";
import ResultList from "./ResultList.vue";
import ErrorList from "./ErrorList.vue";
import axios from "axios";
import { defineComponent } from "vue";
import type {ErrorDto, FileUploadDto, ReportDto} from "../types.ts";

export default defineComponent({
  components: {
    FileUpload,
    FileList,
    ResultList,
    ErrorList,
  },

  data() {
    function generateGuid(): string {
      return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (character) => {
        const random = (Math.random() * 16) | 0;
        const value = character === "x" ? random : (random & 0x3) | 0x8;
        return value.toString(16);
      });
    }

    return {
      files: [] as FileUploadDto[], // List of files
      selectedFile: null as FileUploadDto | null, // Currently selected file
      results: [] as ReportDto[], // Results for the selected file
      errors: [] as ErrorDto[], // Errors for the selected file
      currentView: null as "results" | "errors" | null, // Current view toggle
      eventSource: null as EventSource | null, // SSE connection reference
      reconnectAttempt: 0, // Attempt count for reconnecting
      reconnectDelay: 1000, // Delay between SSE reconnects (ms)
      clientId: generateGuid(), // Unique client identifier for SSE
    };
  },

  created() {
    // Fetch files on component mount
    this.fetchFiles();
    this.initSSE();
  },

  methods: {
    /**
     * Fetch the list of files from the API.
     */
    async fetchFiles(): Promise<void> {
      try {
        const response = await axios.get<FileUploadDto[]>("/api/files");
        this.files = response.data; // Assign fetched files
      } catch (error) {
        console.error("Error fetching files:", error);
        alert("Failed to fetch the list of files.");
        this.files = []; // Reset files list on failure
      }
    },

    /**
     * Add a newly uploaded file to the list.
     * @param {FileUploadDto} file - File to add.
     */
    addFile(file: FileUploadDto): void {
      this.files.push(file);
    },

    /**
     * Handle file deletion.
     * @param {number} fileId - ID of the file to process.
     */
    handleFileDeleted(fileId: number): void {
      console.log(`File with ID ${fileId} was deleted.`);
      this.fetchFiles(); // Refresh the files list after deletion
    },

    /**
     * Fetch results for a specific file.
     * @param {FileUploadDto} file - File to fetch results for.
     */
    async selectFileAndFetchResults(file: FileUploadDto): Promise<void> {
      this.selectedFile = file;
      this.currentView = "results";
      try {
        const response = await axios.get<ReportDto[]>(`/api/files/${file.id}/reports`);
        this.results = response.data; // Assign fetched results
      } catch (error) {
        console.error("Error fetching results:", error);
        alert("Failed to fetch results for the selected file.");
        this.results = []; // Reset results on failure
      }
    },

    /**
     * Fetch errors for a specific file.
     * @param {FileUploadDto} file - File to fetch errors for.
     */
    async selectFileAndFetchErrors(file: FileUploadDto): Promise<void> {
      this.selectedFile = file;
      this.currentView = "errors";
      try {
        const response = await axios.get<ErrorDto[]>(`/api/files/${file.id}/errors`);
        this.errors = response.data; // Assign fetched errors
      } catch (error) {
        console.error("Error fetching errors:", error);
        alert("Failed to fetch errors for the selected file.");
        this.errors = []; // Reset errors on failure
      }
    },

    /**
     * Hide the results view.
     */
    hideResults(): void {
      this.selectedFile = null;
      this.results = [];
      this.currentView = null;
    },

    /**
     * Hide the errors view.
     */
    hideErrors(): void {
      this.selectedFile = null;
      this.errors = [];
      this.currentView = null;
    },

    /**
     * Initialize and manage the SSE connection.
     */
    initSSE(): void {
      console.log("Initializing SSE connection...");
      this.eventSource = new EventSource(`/sse/events?clientId=${this.clientId}`);

      this.eventSource.addEventListener("updateFile", (event) => {
        this.reconnectAttempt = 0; // Reset reconnect attempts
        console.log("updateFile event received.", event.data);
        const updatedFile: FileUploadDto = JSON.parse(event.data);
        this.updateFileInList(updatedFile);
      });

      this.eventSource.onerror = () => {
        console.error("SSE connection lost.");
        this.reconnectAttempt += 1;
        setTimeout(() => {
          this.initSSE(); // Attempt reconnection
        }, this.reconnectDelay);
      };

    /*  this.eventSource.onclose = () => {
        console.warn("SSE connection closed.");
      };*/
    },

    /**
     * Update file details in the list with new data.
     * @param {FileUploadDto} updatedFile - Updated file details.
     */
    updateFileInList(updatedFile: FileUploadDto): void {
      const fileIndex = this.files.findIndex((file) => file.id === updatedFile.id);
      if (fileIndex !== -1) {
        this.files[fileIndex] = { ...this.files[fileIndex], ...updatedFile };
      } else {
        console.warn(`File with ID ${updatedFile.id} not found in the list.`);
      }
    },

    beforeDestroy(): void {
      if (this.eventSource) {
        this.eventSource.close(); // Close SSE connection
      }
    },
  },
});
</script>