<template>
    <div class="container">
        <div class="section">
            <b-navbar>
                <template slot="start">
                    <b-navbar-item>
                        <h1 class="title">{{ userName }}'s stars</h1>
                    </b-navbar-item>
                    <b-navbar-item>
                        <RepoSync/>
                    </b-navbar-item>
                </template>
                <template slot="end">
                    <b-navbar-item>
                        <ul>
                            <li>
                                <b-dropdown position="is-bottom-left" append-to-body trap-focus aria-role="list">
                                    <figure class="image is-32x32" slot="trigger" role="button">
                                        <img class="is-rounded" v-lazy="userAvatarUrl">
                                    </figure>

                                    <b-dropdown-item aria-role="listitem" v-on:click="gotoUpdateProfile()">Update Profile
                                    </b-dropdown-item>
                                    <b-dropdown-item aria-role="listitem">Logout</b-dropdown-item>
                                </b-dropdown>
                            </li>
                        </ul>
                    </b-navbar-item>
                </template>
            </b-navbar>
        </div>
        <b-loading v-if="isLoading" :active="isLoading" :can-cancel="false"/>
        <div class="section">
            <TagsList/>
        </div>
        <div class="section">
            <Repos/>
        </div>
        <div v-if="activeTags.length === 0 && reposToDisplay.length !== 0" class="section">
            <hr>
            <div>
                <b-pagination
                        :total="total"
                        :current.sync="pageNm"
                        :per-page="pageSize"
                        order="is-centered"
                        range-before="3"
                        range-after="1"
                        aria-next-label="Next page"
                        aria-previous-label="Previous page"
                        aria-page-label="Page"
                        aria-current-label="Current page"
                        v-on:change="pageClickCallBack">
                </b-pagination>
            </div>
        </div>
        <hr>
        <section>
            <div>
                <footer class="footer">
                    <div class="content has-text-centered">
                        <p>
                            <strong>TAGGIT</strong> by <a href="https://shiveenp.com">Shiveen Pandita</a>. The source
                            code is
                            licensed
                            <a href="http://opensource.org/licenses/mit-license.php">MIT</a>. The website content
                            is licensed <a href="http://creativecommons.org/licenses/by-nc-sa/4.0/">CC BY NC SA 4.0</a>.
                        </p>
                        <p>
                            <link href="https://fonts.googleapis.com/css?family=Cookie" rel="stylesheet">
                            <a class="bmc-button" target="_blank" href="https://www.buymeacoffee.com/shavz"><img
                                    src="https://cdn.buymeacoffee.com/buttons/bmc-new-btn-logo.svg"
                                    alt="Buy me a coffee"><span style="margin-left:15px;font-size:28px !important;">Buy me a coffee</span></a>
                        </p>
                    </div>
                </footer>
            </div>
        </section>
    </div>
</template>

<script>
  import {mapGetters} from "vuex";
  import axios from "axios";
  import Repos from "../components/Repos";
  import TagsList from "../components/TagsList";
  import RepoSync from "../components/RepoSync";
  import {TAGGIT_BASE_API_URL} from "../common/config";

  export default {
    name: "User",
    components: {TagsList, Repos, RepoSync},
    computed: {
      ...mapGetters(["userName", "email", "githubUserName", "githubUserId", "isLoading", "reposToDisplay", "pageNm", "pageSize", "total", "activeTags",
        "userAvatarUrl"])
    },
    methods: {
      fetchUserDetails() {
        this.$store.dispatch('fetchUser', {userId: this.$route.params.userId});
      },
      pageClickCallBack(pageNm) {
        this.$store.dispatch("changePageNm", pageNm)
        this.$store.dispatch('fetchRepos', {userId: this.$route.params.userId});
      },
      gotoUpdateProfile() {
        this.$router.push({name: 'account', params: {userId: this.$route.params.userId}});
      }
    },
    created() {
      this.fetchUserDetails();
    }
  }
</script>

<style scoped>
    .bmc-button img {
        height: 34px !important;
        width: 35px !important;
        margin-bottom: 1px !important;
        box-shadow: none !important;
        border: none !important;
        vertical-align: middle !important;
    }

    .bmc-button {
        padding: 7px 10px 7px 10px !important;
        line-height: 35px !important;
        height: 51px !important;
        min-width: 217px !important;
        text-decoration: none !important;
        display: inline-flex !important;
        color: #FFFFFF !important;
        background-color: #FF813F !important;
        border-radius: 5px !important;
        border: 1px solid transparent !important;
        padding: 7px 10px 7px 10px !important;
        font-size: 22px !important;
        letter-spacing: 0.6px !important;
        box-shadow: 0px 1px 2px rgba(190, 190, 190, 0.5) !important;
        -webkit-box-shadow: 0px 1px 2px 2px rgba(190, 190, 190, 0.5) !important;
        margin: 0 auto !important;
        font-family: 'Cookie', cursive !important;
        -webkit-box-sizing: border-box !important;
        box-sizing: border-box !important;
        -o-transition: 0.3s all linear !important;
        -webkit-transition: 0.3s all linear !important;
        -moz-transition: 0.3s all linear !important;
        -ms-transition: 0.3s all linear !important;
        transition: 0.3s all linear !important;
    }

    .bmc-button:hover, .bmc-button:active, .bmc-button:focus {
        -webkit-box-shadow: 0px 1px 2px 2px rgba(190, 190, 190, 0.5) !important;
        text-decoration: none !important;
        box-shadow: 0px 1px 2px 2px rgba(190, 190, 190, 0.5) !important;
        opacity: 0.85 !important;
        color: #FFFFFF !important;
    }
</style>
