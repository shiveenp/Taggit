import axios from "axios";

const state = {
    searchKey: ''
};

const getters = {
    searchKey(state) {
        return state.searchKey;
    }
};

const mutations = {
    setSearchKey(state, data) {
        if (data !== undefined && data !== '') {
            state.searchKey = data;
        }
    }
};

const actions = {
    setSearchKey({ commit }, data) {
        commit('fetchingData');
        commit('setSearchKey', data);
        commit('fetchFinished');
    },

    fetchReposUsingSearchKey({commit}, params) {
        commit('fetchingData');
        console.log(`here params is: ${params}`);
        const keysToSearch = params.split(" ");
        axios.post('/api/repos/search', {
            keys: keysToSearch
        }).then(({data}) => {
            commit('setActiveSearchRepoData', data);
            commit('fetchFinished');
        }).catch(error => {
            commit('fetchFinished');
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
