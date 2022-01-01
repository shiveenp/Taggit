import axios from "axios";
import qs from "qs";

const state = {
    isSyncing: false,
    repos: [],
    reposToDisplay: [],
    pageNm: '1',
    pageSize: '50',
    total: ''
};

const getters = {
    isSyncing(state) {
        return state.isSyncing;
    },
    repos(state) {
        return state.repos;
    },
    reposToDisplay(state) {
        return state.reposToDisplay;
    },
    total(state) {
        return state.total;
    },
    pageNm(state) {
        return state.pageNm;
    },
    pageSize(state) {
        return state.pageSize;
    }
};

const mutations = {
    changeIsSyncing(state, data) {
        state.isSyncing = data;
    },
    getRepoData(state, data) {
        state.repos = data.data;
        state.reposToDisplay = data.data;
        state.total = data.total;
    },
    getActiveTagRepoData(state, data) {
        state.reposToDisplay = data
    },
    changePageNm(state, data) {
        state.pageNm = data;
    }
};

const actions = {
    resyncRepos({commit}, params) {
        commit("changeIsSyncing", true)
        axios.get("/api/repos/sync")
            .then(response => {
                params.vmInstance.$buefy.toast.open('Repo sync initiated in the background');
                commit("changeIsSyncing", false);
            }).catch(function (error) {
            console.log(error);
            commit("changeIsSyncing", false);
        });
    }
    ,
    fetchRepos({commit}, params) {
        commit('fetchingData');
        axios.get('/api/repos' + '?pageNm=' + (state.pageNm - 1) + '&pageSize=' + state.pageSize)
            .then(({data}) => {
                commit('getRepoData', data);
                commit('fetchFinished')
            })
            .catch(error => {
                commit('fetchFinished');
                throw new Error(error);
            });
    },
    fetchReposUsingTags({commit}, params) {
        commit('fetchingData');
        axios.get('/api/repos/search', {
            params: {
                tag: params.tags
            },
            paramsSerializer: function (params) {
                return qs.stringify(params, {arrayFormat: 'repeat'})
            },
        }).then(({data}) => {
            commit('getActiveTagRepoData', data);
            commit('fetchFinished')
        })
            .catch(error => {
                commit('fetchFinished');
                throw new Error(error);
            });
    },
    fetchUntaggedRepos({commit}, params) {
        commit('fetchingData');
        axios.get('/api/repos/untagged')
            .then(({data}) => {
                commit('getActiveTagRepoData', data);
                commit('fetchFinished')
            })
            .catch(error => {
                commit('fetchFinished');
                throw new Error(error);
            });
    },
    addTagToRepo({commit}, params) {
        axios.post('/api/repos/' + params.repoId + '/tag',
            {
                tag: params.tag
            });
    },
    changePageNm({commit}, data) {
        commit('changePageNm', data);
    }
};

export default {
    state,
    getters,
    actions,
    mutations
};
