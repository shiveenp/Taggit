<template>
  <div class="card">
    <div class="card-content">
      <div class="media">
        <div class="media-left">
          <figure class="image is-32x32">
            <img class="is-rounded" v-lazy="ownerAvatarUrl">
          </figure>
        </div>
        <div class="media-content">
          <a class="has-text-weight-bold" target="_blank" rel="noopener noreferrer" :href="githubLink">{{
              repoName
            }}</a>
        </div>
      </div>
      <div class="content">
        <p>{{ githubDescription }}</p>
      </div>
      <div>
          <b-taginput
              v-model="tags"
              autocomplete
              :data="filteredTags"
              :allow-new="true"
              ellipsis
              keep-first
              placeholder="Add a tag"
              size="is-small"
              type="is-dark"
              v-on:add="saveTag"
              v-on:remove="removeTag"
              @typing="getFilteredTags">
          </b-taginput>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import {mapGetters} from "vuex";

export default {
  data() {
    return {
      tags: [],
      filteredTags: this.allTags,
    }
  },
  computed: {
    ...mapGetters(["allTags"])
  },
  props: ['id', 'repoName', 'githubLink', 'githubDescription', 'ownerAvatarUrl', 'metadata'],
  name: "GithubRepo",
  methods: {
    saveTag(tag) {
      let sanitizedTag = this.sanitizeTag(tag);
      this.$store.dispatch('addTagToRepo',
          {
            repoId: this.id,
            tag: sanitizedTag
          });
      this.$store.dispatch('addTag', sanitizedTag);
    },
    removeTag(tag) {
      axios.delete('/api/repos/' + this.id + '/tag?tag=' + tag);
      this.$store.dispatch('removeTag', tag);
    },
    mountTags() {
      if (!(this.metadata === undefined) && !(this.metadata === null)) {
        this.tags = this.metadata.tags;
      }
    },
    sanitizeTag(tag) {
      return tag.replace(/[^a-zA-Z0-9]/g, '')
    },
    getFilteredTags(text) {
      this.filteredTags = this.allTags.filter((tag) => {
        return tag
            .toString()
            .toLowerCase()
            .indexOf(text.toLowerCase()) >= 0
      })
    }
  },
  mounted() {
    this.mountTags();
  }
}
</script>

<style scoped>
.card {
  height: 100%;
}
</style>
