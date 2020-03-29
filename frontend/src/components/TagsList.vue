<template>
    <div class="container is-fluid">
        <div class="buttons">
            <TagButton v-for="tag in tags" v-bind:key="tag" v-bind:tag="tag"></TagButton>
        </div>
    </div>
</template>

<script>
  import TagButton from "./TagButton";
  import axios from "axios";
  import { TAGGIT_BASE_API_URL } from "../common/config";

  export default {
    name: "TagsList",
    components: {TagButton},
    data() {
      return {
        tags: []
      }
    },
    methods: {
      fetchAllTags() {
        axios.get(TAGGIT_BASE_API_URL + '/user/' + this.$route.params.userId + '/tags', {
          headers: {
            'Content-Type': 'application/json'
          }
        }).then(({data}) => {
          this.tags = data;
        })
            .catch(error => {
              throw new Error(error);
            });
      },
    },
    mounted() {
      this.fetchAllTags()
    }
  }
</script>

<style scoped>

</style>
