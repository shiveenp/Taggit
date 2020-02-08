<template>
    <div class="container is-fluid">
        <div class="section">
            <b-loading :active="isLoading" :can-cancel="false"></b-loading>
            <h1>Welcome to Gitstar: {{ userName }}</h1>
        </div>
    </div>
</template>

<script>
  import {mapGetters} from "vuex";

  export default {
    name: "User",
    computed: {
      ...mapGetters(["userName", "email", "githubUserName", "githubUserId", "isLoading"])
    },
    methods: {
      fetchUserDetails() {
        console.log('here');
        this.$store.dispatch('fetchUser', { userId: this.$route.params.userId});
      }
    },
    mounted() {
      this.fetchUserDetails();
    }
  }
</script>

<style scoped>

</style>
