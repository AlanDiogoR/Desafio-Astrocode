<template>
  <v-main class="pa-0">
    <v-container fluid class="auth-layout pa-0 ma-0 fill-height" style="overflow-x: hidden">
        <v-row no-gutters class="fill-height">
          <!-- Coluna Esquerda - Formulário (50% desktop) -->
          <v-col
            cols="12"
            md="6"
            lg="6"
            class="d-flex flex-column align-center justify-center bg-white"
            style="min-height: 100vh"
          >
            <div class="auth-form-wrapper pa-6 pa-md-12 d-flex flex-column justify-center mx-auto" style="width: 100%; max-width: 450px">
              <slot />
            </div>
          </v-col>

          <!-- Coluna Direita - Hero/Marketing (50% desktop, oculta em mobile) -->
          <v-col
            cols="12"
            md="6"
            lg="6"
            class="d-none d-md-flex auth-visual align-stretch"
          >
            <div class="auth-visual__content d-flex flex-column align-center justify-center px-8 py-12">
              <!-- Ilustração centralizada (Login.png) -->
              <div class="auth-visual__image-container mb-10">
                <v-img
                  src="/images/Login.png"
                  contain
                  max-height="360"
                  class="auth-visual__image"
                />
              </div>

              <!-- Branding promocional abaixo da imagem -->
              <div class="auth-visual__branding text-center" style="max-width: 420px">
                <div class="d-flex align-center justify-center mb-4">
                  <img src="~/assets/images/Bank Card double 1.png" alt="Grivy" style="height: 28px; width: auto; margin-right: 8px;" />
                  <span class="text-h5 font-weight-bold" style="color: #087F5B">Grivy</span>
                </div>
                <p class="text-body-1" style="color: #495057; line-height: 1.6">
                  Gerencie suas finanças pessoais de uma forma simples com o Grivy, e o melhor, totalmente de graça!
                </p>
              </div>
            </div>
          </v-col>
      </v-row>
    </v-container>
  </v-main>
</template>

<style scoped>
.auth-layout {
  min-height: 100vh;
  overflow-x: hidden;
}

.auth-visual {
  position: relative;
  min-height: 100vh;
  background: var(--auth-hero-bg, linear-gradient(135deg, #E6FCF5 0%, #C3FAE8 50%, #96F2D7 100%));
}

.auth-visual__content {
  width: 100%;
  flex: 1;
  min-height: 0;
}

.auth-visual__image-container {
  width: 100%;
  max-width: 480px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.auth-visual__image {
  object-fit: contain;
  object-position: center;
}
</style>
