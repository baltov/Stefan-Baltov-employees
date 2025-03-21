<template>
  <div id="app" class="bg-gray-100 min-h-screen p-6">
    <Main />
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import Main from "./components/Main.vue";


export default defineComponent({
  name: "App",
  components: {
    Main,
  },
});
</script>

<style>
</style>