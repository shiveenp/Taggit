<template>
  <b-field>
    <b-input placeholder="Search for repos"
             type="search"
             icon="magnify"
             icon-clickable
             v-model="searchKey"
             v-on:keyup.native.enter="searchRepos"
             @icon-click="searchRepos">
    </b-input>
  </b-field>
</template>

<script>
export default {
  name: "RepoSearch",
  data() {
    return {
      searchKey: '',
    }
  },
  methods: {
    searchRepos() {
      // this.$store.dispatch('setSearchKey', this.searchKey);
      this.$store.dispatch('fetchReposUsingSearchKey', this.searchKey);
    }
  }
}
</script>

<style scoped>

</style>