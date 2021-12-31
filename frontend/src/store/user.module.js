import axios from "axios";
import {TAGGIT_BASE_API_URL} from "@/common/config";

const state = {
    id: '',
    userName: '',
    email: '',
    githubUserName: '',
    githubUserId: '',
    userAvatarUrl: '',
    isLoading: true,
};

const getters = {
    userName(state) {
        return state.userName
    },
    email(state) {
        return state.email
    },
    githubUserName(state) {
        return state.githubUserName
    },
    githubUserId(state) {
        return state.githubUserId
    },
    userAvatarUrl(state) {
        return state.userAvatarUrl;
    },
    isLoading(state) {
        return state.isLoading
    }
};

const mutations = {
    fetchingData(state) {
        state.isLoading = true
    },
    getUserData(state, data) {
        state.id = data.id;
        state.userName = data.userName;
        state.email = data.email;
        state.githubUserName = data.githubUserName;
        state.githubUserId = data.githubUserId;
        state.userAvatarUrl = data.avatarUrl;
        state.isLoading = false;
    },
    fetchFinished(state) {
        state.isLoading = false
    }
};

const actions = {
    fetchUser({commit}, params) {
        commit('fetchingData');
        axios.get(TAGGIT_BASE_API_URL + '/user/')
            .then(({data}) => {
                commit('getUserData', data);
            })
            .catch(error => {
                throw new Error(error);
            });
    }
};

export default {
    state,
    getters,
    actions,
    mutations
};
