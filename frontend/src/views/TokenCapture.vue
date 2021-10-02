
<template>
  <div>"Please wait..."</div>
</template>

<script>
export default {
  name: "TokenCapture",
  methods: {
    persistTokenAndRedirectToHome() {
      let userId = this.$route.params.userId
      let token = this.$route.query.token
      localStorage.setItem("taggit-session-token", token)
      localStorage.setItem("taggit-userId", userId)
      this.$router.push(`/home`)
    }
  },
  created() {
    this.persistTokenAndRedirectToHome()
  }
}
</script>

<style scoped>

</style>
