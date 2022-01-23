<template>
  <b-field>
    <b-input placeholder="Search for repos"
             type="search"
             icon="magnify"
             icon-clickable
             @icon-click="searchIconClick">
    </b-input>
  </b-field>
</template>

<script>
export default {
  name: "RepoSearch",
  methods: {
    searchIconClick() {

    }
  }
}
</script>

<style scoped>

</style>