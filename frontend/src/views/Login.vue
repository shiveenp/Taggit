<template>
  <div>
    Logging you in...
  </div>
</template>

<script>
  import { TAGGIT_BASE_API_URL } from "@/common/config";

  export default {
    name: "Login",
    methods: {
      login: function () {
        window.location = `${TAGGIT_BASE_API_URL}/signin`;
      }
    },
    created() {
      this.login();
    }
  };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
