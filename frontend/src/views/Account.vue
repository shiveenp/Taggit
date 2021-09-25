<template>
    <div class="container">
        <div class="section">
            <b-button type="is-link" icon-left="arrow-left" v-on:click="goBack">
                Go Back
            </b-button>
        </div>
        <div class="section">
            <div class="card">
                <header class="card-header">
                    <p class="card-header-title">
                        Personal Information
                    </p>
                </header>
                <div class="card-content">
                    <div class="media">
                        <div class="media-left">
                            <figure class="image is-48x48">
                                <img class="is-rounded" :src="userAvatarUrl" alt="">
                            </figure>
                        </div>
                        <div class="media-content">
                            <p class="title is-4">{{ userName }}</p>
                            <p class="subtitle is-6"><a target="_blank" rel="noopener noreferrer"
                                                        :href="userGithubLink">@ {{ githubUserName }}</a></p>
                        </div>
                    </div>
                    <div class="content">
                        <b-field label="User Name" label-position="on-border">
                            <b-input :disabled="isInputDisabled"
                                     ref="userNameInput"
                                     type="text"
                                     required
                                     validation-message="Username is not valid"
                                     :placeholder="userName"
                                     v-model="inputUserName"></b-input>
                        </b-field>

                        <b-field label="Email" label-position="on-border">
                            <b-input :disabled="isInputDisabled"
                                     ref="userEmailInput"
                                     type="email"
                                     :placeholder="email"

                                     v-model="inputEmail">
                            </b-input>
                        </b-field>
                        <b-field class="is-grouped">
                            <div class="control">
                                <button class="button is-primary" v-if="isInputDisabled === true"
                                        v-on:click="enablePersonalInfoInput">Edit Info
                                </button>
                            </div>
                            <div class="control">
                                <button class="button is-primary"
                                        v-if="isInputDisabled === false"
                                        v-on:click="updatePersonalInfo()">Save
                                </button>
                            </div>
                            <div class="control">
                                <button class="button is-primary" v-if="isInputDisabled === false"
                                        v-on:click="disablePersonalInfoInput">Cancel
                                </button>
                            </div>
                        </b-field>
                    </div>
                </div>
            </div>
            <hr>
            <div class="section">
                <p class="subtitle">Saying goodbye? 😞</p>
                <b-button
                        v-if="!isUserGettingDeleted"
                        type="is-danger"
                        @click="deleteUserAccount"
                >Delete account and any existing data
                </b-button>
                <b-button
                        loading
                        v-if="isUserGettingDeleted"
                        type="is-danger"
                        @click="deleteUserAccount"
                >Delete account and any existing data
                </b-button>
            </div>
        </div>
    </div>
</template>

<script>
  import {TAGGIT_BASE_API_URL} from "../common/config";
  import {mapGetters} from "vuex";
  import axios from 'axios';

  export default {
    name: "UpdateProfile",
    computed: {
      userGithubLink: function () {
        return 'https://github.com/' + this.githubUserName;
      },
      ...mapGetters(["userName", "email", "githubUserName", "userAvatarUrl"])
    },
    data() {
      return {
        isInputDisabled: true,
        inputUserName: '',
        inputEmail: '',
        isUserGettingDeleted: false,
      }
    },
    methods: {
      fetchUserDetails() {
        this.$store.dispatch('fetchUser', {userId: this.$route.params.userId});
      },
      enablePersonalInfoInput() {
        this.isInputDisabled = false;
        this.inputUserName = this.userName;
        this.inputEmail = this.email;
      },
      disablePersonalInfoInput() {
        this.isInputDisabled = true;
        this.inputUserName = '';
        this.inputEmail = '';
      },
      updatePersonalInfo() {
        if (!this.$refs.userNameInput.checkHtml5Validity() || !this.$refs.userEmailInput.checkHtml5Validity()) {
          this.$buefy.toast.open({
            message: 'Please fix errors before clicking save',
            type: 'is-danger'
          });
          return
        }
        axios.put(TAGGIT_BASE_API_URL + '/user/' + this.$route.params.userId, {
              userName: this.inputUserName,
              email: this.inputEmail
            },
            {
              headers: {
              'Content-Type': 'application/json',
              "x-taggit-session-key": localStorage.getItem("taggit-session-token")
              }
            }).then(response => {
          if (response.status === 200) {
            this.$buefy.toast.open({
              message: 'Personal information successfully updated!',
              type: 'is-success'
            });
          } else {
            this.$buefy.toast.open({
              message: 'Unable to update personal information right now',
              type: 'is-danger'
            });
          }
          this.isInputDisabled = true;
          this.fetchUserDetails();
        }).catch(error => {
          this.$buefy.toast.open({
            message: 'Unable to update personal information right now',
            type: 'is-danger'
          });
          console.log(error);
        });
      },
      goBack() {
        this.$router.go(-1);
      },
      deleteUserAccount() {
        this.isUserGettingDeleted = true;
        axios.delete(TAGGIT_BASE_API_URL + '/user/' + this.$route.params.userId)
            .then((response) => {
              this.isUserGettingDeleted = false;
              this.$buefy.toast.open({
                message: 'Account delete successfull',
                type: 'is-success'
              });
            }).catch((error) => {
              this.isUserGettingDeleted = false;
          this.$buefy.toast.open({
            message: 'Unexpected error while deleting user, please try again later',
            type: 'is-danger'
          });
          console.log(error);
        })
      }
    },
    created() {
      this.fetchUserDetails();
    }
  }
</script>

<style scoped>

</style>
