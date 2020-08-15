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
      console.log(`userId is ${userId}`)
      console.log(`token is ${token}`)
      localStorage.setItem("taggit-user-session-token", token)
      this.$router.push(`/user/${userId}`)
    }
  },
  created() {
    this.persistTokenAndRedirectToHome()
  }
}
</script>

<style scoped>

</style>
