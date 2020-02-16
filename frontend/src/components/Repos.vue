<template>
    <div class="container is-fluid">
        <div class="columns is-multiline is-mobile">
            <div class="column" v-for="repo in reposList" v-bind:key="repo">
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
  import axios from "axios";

  export default {
    name: "Repos",
    components: {GithubRepo},
    data() {
      return {
        reposList: [],
      }
    },
    methods: {
      fetchUserRepos() {
        axios.get('http://localhost:9001/user/' + this.$route.params.userId + '/repos', {
          headers: {
            'Content-Type': 'application/json'
          }
        }).then(({data}) => {
          this.reposList = data;
        })
            .catch(error => {
              throw new Error(error);
            });
      }
    },
    created() {
      this.fetchUserRepos();
    }
  }
</script>

<style scoped>

</style>
