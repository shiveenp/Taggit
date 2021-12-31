<template>
    <div>
        <div class="columns is-multiline is-mobile">
            <div class="column is-one-third" v-for="repo in reposToDisplay" :key="repo.id">
                <GithubRepo
                        :id="repo.id"
                        :repo-name="repo.repoName"
                        :github-link="repo.githubLink"
                        :github-description="repo.githubDescription"
                        :owner-avatar-url="repo.ownerAvatarUrl"
                        :metadata="repo.metadata"
                />
            </div>
        </div>
        <div v-if="reposToDisplay.length === 0" class="hero">
            <div class="hero-body">
                <div class="container">
                    <h3 class="title is-centered no-repos">
                        Click Sync above to get started
                    </h3>
                </div>
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
    computed: {
      ...mapGetters(["repos", "reposToDisplay", "activeTags"])
    },
    watch: {
      // eslint-disable-next-line no-unused-vars
      activeTags(newValue, oldValue) {
        if (newValue.length > 0) {
          this.$store.dispatch('fetchReposUsingTags', {tags: newValue});
        } else {
          this.$store.dispatch('fetchRepos');
        }
      }
    },
    methods: {
      fetchUserRepos() {
        this.$store.dispatch('fetchRepos');
      }
    },
    created() {
      this.fetchUserRepos();
    }
  }
</script>

<style scoped>
    .no-repos {
        text-align: center;
    }
    .columns {
        display: flex;
        flex-direction: row;
        align-items: stretch;
    }
</style>
