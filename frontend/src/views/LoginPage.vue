<template>
  <div>
    <div class="section is-centered">
      <div class="container">
        <div class="title has-text-centered">
          Welcome to Taggit
        </div>
      </div>
    </div>
    <div class="section">
      <div class="container">
        <div class="columns is-flex is-centered">
          <div class="column is-half">
            <div class="box">
              <b-field label="Password">
                <b-input type="password"
                         placeholder="********"
                         v-model="password"
                         v-on:keyup.native.enter="login"
                         password-reveal
                />
              </b-field>
              <b-button class="button is-primary" @click="login">Login</b-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "LoginPage",

  data() {
    return {
      password: '',
    }
  },

  methods: {
    login() {
      axios.get('/api/login?password=' + this.password)
      .then(() => {
        localStorage.setItem('taggit:loggedIn', 'true')
        window.location = '/'
      }).catch(() => {
        console.error('Unable to login with the given password')
        this.$buefy.snackbar.open(`🛑 Password is incorrect`)
      })

    }
  }
}
</script>

<style scoped>

</style>