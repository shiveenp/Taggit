<template>
  <div class="flex-container">
    <div class="container">
      <h1>Welcome To Taggit</h1>
      <form class="form">
        <b-button size="is-medium" icon-left="github-circle" @click="login">
          Login with Github
        </b-button>
      </form>
    </div>
  </div>
</template>

<script>
  import { TAGGIT_BASE_API_URL } from "../common/config";

  export default {
    methods: {
      login: function () {
        window.location = `${TAGGIT_BASE_API_URL}/signin`;
      }
    }
  };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  body {
    font-family: 'Source Sans Pro', sans-serif;
    color: white;
    font-weight: 300;
  }
  body ::-webkit-input-placeholder {
    /* WebKit browsers */
    font-family: 'Source Sans Pro', sans-serif;
    color: white;
    font-weight: 300;
  }
  body :-moz-placeholder {
    /* Mozilla Firefox 4 to 18 */
    font-family: 'Source Sans Pro', sans-serif;
    color: white;
    opacity: 1;
    font-weight: 300;
  }
  body ::-moz-placeholder {
    /* Mozilla Firefox 19+ */
    font-family: 'Source Sans Pro', sans-serif;
    color: white;
    opacity: 1;
    font-weight: 300;
  }
  body :-ms-input-placeholder {
    /* Internet Explorer 10+ */
    font-family: 'Source Sans Pro', sans-serif;
    color: white;
    font-weight: 300;
  }
  .wrapper.form-success .container h1 {
    -webkit-transform: translateY(85px);
    transform: translateY(85px);
  }

  .flex-container {
    width: 100vw;

    margin-top: 60px;

    display: flex;
    justify-content: center;
    align-items: center;
  }

  .container {
    max-width: 600px;
    margin: 0 auto;
    padding: 80px 0;
    height: 400px;
    text-align: center;
  }
  .container h1 {
    font-size: 40px;
    -webkit-transition-duration: 1s;
    transition-duration: 1s;
    -webkit-transition-timing-function: ease-in-put;
    transition-timing-function: ease-in-put;
    font-weight: 200;
  }
  form {
    padding: 20px 0;
    position: relative;
    z-index: 2;
  }

</style>
