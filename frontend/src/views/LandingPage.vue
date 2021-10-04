<template>
  <div class="container">
    <div class="section">
      <h1 class="title">Taggit <img alt="Vue logo" src="../assets/logo-60x60.png"/></h1>
      <h2 class="subTitle">A simple way to manage your favourite git repos</h2>
    </div>
    <div class="section">
      <b-button type="is-dark" @click="getStartedClick">Get Started</b-button>
    </div>
  </div>
</template>

<script>
export default {
  methods: {
    getStartedClick() {
      this.$router.push(`/login`)
    }
  }
}
</script>


<style scoped>

</style>
