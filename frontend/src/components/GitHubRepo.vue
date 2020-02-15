<template>
    <div class="card">
        <div class="card-content">
            <div class="media">
                <div class="media-left">
                    <figure class="image is-32x32">
                        <img class="is-rounded" :src="ownerAvatarUrl">
                    </figure>
                </div>
                <div class="media-content">
                    <a class="has-text-weight-bold" :href="githubLink">{{ repoName }}</a>
                </div>
            </div>
            <div class="content">
                <p>{{ githubDescription }}</p>
            </div>
        </div>
    </div>
</template>

<script>
  export default {
    props: ['repoName', 'githubLink', 'githubDescription', 'ownerAvatarUrl'],
    name: "GithubRepo"
  }
</script>

<style scoped>

</style>
