<template>
  <router-view></router-view>
</template>

<script setup>
</script>

<style>
/* Global styles */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden;
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE/Edge */
}

html::-webkit-scrollbar,
body::-webkit-scrollbar {
  display: none; /* Chrome/Safari */
}

body {
  min-height: 100vh;
  box-sizing: border-box;
  font-family: 'Inter', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

* {
  box-sizing: border-box;
}
</style>
