<template>
    <div class="container is-fluid">
        <div class="section">
            <b-loading :active="isLoading" :can-cancel="false"></b-loading>
            <h1 class="title">Welcome, {{ userName }}! 🤩</h1>
        </div>
        <div class="section">
            <b-tooltip class="is-pulled-right" label="Sync starred repos with github again">
                <button class="button is-primary has-tooltip" data-tooltip="Tooltip Text">
                    <b-icon icon="reload"></b-icon>
                    <span v-on:click="resyncRepos">Resync</span>
                </button>
            </b-tooltip>
        </div>
        <div class="section">
            <TagsList></TagsList>
        </div>
        <div class="section">
            <Repos></Repos>
        </div>
        <footer class="footer">
            <div class="content has-text-centered">
                <p>
                    <strong>Gitstars</strong> by <a href="https://shiveenp.com">Shiveen Pandita</a>. The source code is
                    licensed
                    <a href="http://opensource.org/licenses/mit-license.php">MIT</a>. The website content
                    is licensed <a href="http://creativecommons.org/licenses/by-nc-sa/4.0/">CC BY NC SA 4.0</a>.
                </p>
            </div>
        </footer>
    </div>
</template>

<script>
  import {mapGetters} from "vuex";
  import Repos from "./Repos";
  import TagsList from "./TagsList";
  import axios from "axios";
  import { TAGGIT_BASE_API_URL } from "../common/config";

  export default {
    name: "User",
    components: {TagsList, Repos},
    computed: {
      ...mapGetters(["userName", "email", "githubUserName", "githubUserId", "isLoading"])
    },
    methods: {
      fetchUserDetails() {
        this.$store.dispatch('fetchUser', {userId: this.$route.params.userId});
      },
      resyncRepos() {
        axios.post(TAGGIT_BASE_API_URL + "/user/" + this.$route.params.userId + "/sync" )
      }
    },
    mounted() {
      this.fetchUserDetails();
    }
  }
</script>

<style scoped>

</style>
