<template>
  <section>
    <b-sidebar
        type="is-light"
        :fullheight=true
        position="static"
        open
    >
      <div class="buttons">
        <TagButton v-for="tag in allTags" v-bind:key="tag" v-bind:tag="tag"></TagButton>
      </div>
    </b-sidebar>
  </section>
</template>

<script>
import TagButton from "@/components/TagButton";
import {mapGetters} from "vuex";

export default {
  name: "TagSidebar",
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