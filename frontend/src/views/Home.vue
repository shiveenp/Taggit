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
    </div>
</template>

<script>
  import {mapGetters} from "vuex";
  import Repos from "../components/Repos";
  import TagsList from "../components/TagsList";
  import RepoSync from "../components/RepoSync";

  export default {
    name: "Home",
    components: {TagsList, Repos, RepoSync},
    computed: {
      ...mapGetters(["userName", "email", "githubUserName", "githubUserId", "isLoading", "reposToDisplay", "pageNm", "pageSize", "total", "activeTags",
        "userAvatarUrl"])
    },
    methods: {
      fetchUserDetails() {
        this.$store.dispatch('fetchUser');
      },
      pageClickCallBack(pageNm) {
        this.$store.dispatch("changePageNm", pageNm)
        this.$store.dispatch('fetchRepos');
      },
      gotoUpdateProfile() {
        this.$router.push({name: 'account'});
      }
    },
    created() {
      this.fetchUserDetails();
    }
  }
</script>

<style scoped>
</style>
