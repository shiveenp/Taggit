<template>
    <b-button rounded
              :type="buttonType"
              @click="buttonClicked">{{ tag }}</b-button>
</template>

<script>
  export default {
    name: "TagButton",
    props: ['tag'],
    data() {
      return {
        buttonType: 'is-light'
      }
    },
    methods: {
      buttonClicked() {
         if (this.buttonType === 'is-light') {
           this.buttonType = 'is-dark';
           this.$store.dispatch('activateTag', this.tag);
         } else {
           this.buttonType = 'is-light';
           this.$store.dispatch('deactivateTag', this.tag);
         }
      }
    }
  }
</script>

<style scoped>

</style>
