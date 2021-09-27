<template>
  <div class="container">
    <section class="section">
      <h1 class="title">Taggit <img alt="Vue logo" src="../assets/logo-60x60.png"/></h1>
      <h2 class="subTitle">A simple way to manage your favourite git repos</h2>
    </section>
    <section class="section">
      <b-button type="is-dark" @click="getStartedClick">Get Started</b-button>
    </section>
  </div>
</template>

<script>
export default {
  methods: {
    getStartedClick() {
      window.location.href = "http://localhost:8081"
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
