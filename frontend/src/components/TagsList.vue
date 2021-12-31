<template>
    <div class="buttons">
        <TagButton v-for="tag in allTags" v-bind:key="tag" v-bind:tag="tag"></TagButton>
    </div>
</template>

<script>
  import TagButton from "./TagButton";
  import {mapGetters} from "vuex";

  export default {
    name: "TagsList",
    components: {TagButton},
    computed: {
      ...mapGetters(["allTags"])
    },
    methods: {
      fetchAllTags() {
        this.$store.dispatch('fetchAllTags');
      },
    },
    mounted() {
      this.fetchAllTags();
    }
  }
</script>

<style scoped>

</style>
