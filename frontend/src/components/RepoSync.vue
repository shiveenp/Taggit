 <template>
    <div>
        <b-button v-if="!isSyncing" type="submit" size="is-small" class="button is-primary" icon-left="sync"
                @click="resyncRepos">Sync
        </b-button>
        <b-button loading v-if="isSyncing" size="is-small" class="button is-primary" icon-left="sync"
                @click="resyncRepos">Sync
        </b-button>
    </div>
</template>

<script>
  import {mapGetters} from "vuex"

  export default {
    name: "RepoSync",
    computed: {
      ...mapGetters(["isSyncing", "syncProgressPercent", "syncStatus"])
    },
    methods: {
      resyncRepos() {
        this.$store.dispatch('resyncRepos', {vmInstance: this, userId: this.$route.params.userId});
      }
    }
  }
</script>

<style scoped>

</style>
