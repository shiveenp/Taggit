<template>
    <div class="container is-fluid">
        <div class="columns is-multiline is-mobile">
            <div class="column" v-for="repo in repos" v-bind:key="repo">
                <GithubRepo v-bind:id="repo.id"
                            v-bind:repo-name="repo.repoName"
                            v-bind:github-link="repo.githubLink"
                            v-bind:github-description="repo.githubDescription"
                            v-bind:owner-avatar-url="repo.ownerAvatarUrl"
                            v-bind:metadata="repo.metadata"></GithubRepo>
            </div>
        </div>
    </div>
</template>

<script>
  import GithubRepo from "./GitHubRepo";
  import {mapGetters} from "vuex";

  export default {
    name: "Repos",
    components: {GithubRepo},
    data() {
      return {
        repoListToDisplay: [],
        reposList: [],
      }
    },
    computed: {
      ...mapGetters(["repos", "activeTags"])
    },
    watch: {
      // eslint-disable-next-line no-unused-vars
      activeTags(newValue, oldValue) {
        if (newValue.length > 0) {
          this.repoListToDisplay = this.reposList.filter(function (repo) {
            return (repo.metadata !== null && (repo.metadata.tags.some(r => newValue.includes(r))))
          });
        } else {
          this.repoListToDisplay = this.reposList;
        }
      }
    },
    methods: {
      fetchUserRepos() {
        this.$store.dispatch('fetchRepos', {userId: this.$route.params.userId});
      }
    },

    created() {
      this.fetchUserRepos();
    }
  }
</script>

<style scoped>

</style>
