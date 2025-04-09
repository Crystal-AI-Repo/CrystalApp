<script setup lang="ts">

</script>

<template>
  <main class="auth-container-wrapper">
    <div class="auth-container">
      <div class="view-container">
        <RouterView />
      </div>
    </div>
    <div class="background-container"></div>
  </main>
</template>

<style scoped>
.auth-container-wrapper {
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

.background-container {
  width: 100%;
  height: 100%;
  background-image: url("@/assets/ec4f3ed2c4f5deeacd763e45ebc10596.png");
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;
  filter: blur(5rem);
  transform: scale(1.25);
}

.auth-container {
  width: 100%;
  height: 100%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  z-index: 999;
}

.view-container {
  width: 30%;
  min-width: 280px;
  max-width: 400px;
  padding: 1rem;
  background-color: rgba(255, 255, 255, .95);
  border-radius: .5rem;
  box-shadow: 0 0 2rem 1px rgba(0, 0, 0, .15);
  transition-duration: var(--transition-duration-normal);
}

.view-container:hover {
  transition-duration: var(--transition-duration-normal);
  background-color: rgba(255, 255, 255, 1);
}
</style>