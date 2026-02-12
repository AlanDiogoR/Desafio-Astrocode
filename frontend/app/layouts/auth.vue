<template>
  <v-main class="pa-0 ma-0 overflow-hidden">
    <v-container
      fluid
      class="pa-0 ma-0 overflow-hidden"
      style="overflow: hidden !important; height: 100vh;"
    >
      <v-row no-gutters class="fill-height">
        <v-col
          cols="12"
          md="6"
          class="auth-form-side d-flex flex-column align-center justify-center bg-white px-6 py-6 px-md-16 py-md-8 px-xl-16"
        >
          <slot />
        </v-col>

        <v-col
          cols="0"
          md="6"
          class="d-none d-md-flex auth-hero-side flex-column align-center justify-center text-center"
        >
          <div class="auth-hero-inner d-flex flex-column align-center justify-center text-center w-100">
            <div class="auth-hero-image mb-8">
              <v-img
                src="/images/Login.png"
                cover
                max-width="520"
                height="400"
                class="rounded-xl"
              />
            </div>
            <div class="d-flex justify-center mb-4">
              <AppLogo color="#087F5B" :size="32" />
            </div>
            <p class="auth-hero-text text-h6 font-weight-regular">
              Gerencie suas finanças pessoais de uma forma simples com o Grivy, e o melhor, totalmente de graça!
            </p>
          </div>
        </v-col>
      </v-row>
    </v-container>
  </v-main>
</template>

<style scoped>
.auth-form-side {
  background-color: #FFFFFF !important;
  height: 100vh;
  overflow: hidden;
}

.auth-hero-side {
  background: linear-gradient(135deg, #E6FCF5 0%, #C3FAE8 50%, #96F2D7 100%);
  height: 100vh;
  overflow: hidden;
}

.auth-hero-inner {
  gap: 32px;
}

.auth-hero-text {
  color: #495057;
  line-height: 1.6;
  max-width: 580px;
}

.auth-hero-image {
  width: 100%;
  max-width: 520px;
}

@media (min-width: 960px) and (max-width: 1280px) {
  .auth-hero-image :deep(.v-img) {
    max-width: 400px;
  }
}
</style>
