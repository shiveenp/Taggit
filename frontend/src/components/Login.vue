<template>
  <div>
    <div class="buttons">
      <b-button onclick="location.href='http://localhost:9000/login'" size="is-medium"
                icon-left="github-circle">
        Login with Github
      </b-button>
    </div>
  </div>
</template>

<script>
  export default {
    methods: {
      login: function () {
        this.$http.get("http://localhost:9000/login2").catch(error => {
          // eslint-disable-next-line no-console
          console.log(error.response);
          throw new Error('Unable to access gitstars api');
        });
      }
    }
  };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
